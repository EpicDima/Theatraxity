"use strict";

import getString from "../common/localization.js";
import {doPost} from "../common/api.js";
import {openPage} from "../common/Router.js";
import {checkCookies} from "../common/common.js";

export function configurePresentationPage() {
    document.getElementById("presentationNameTitle").innerText = getString("playName");
    document.getElementById("presentationDateTitle").innerText = getString("presentationDate");
    document.getElementById("presentationAuthorTitle").innerText = getString("author");
    document.getElementById("presentationGenreTitle").innerText = getString("genre");
    document.getElementById("presentationDescriptionTitle").innerText = getString("playDescription");
    document.getElementById("totalCostTitle").innerText = getString("totalCost");
    document.getElementById("acceptButton").innerText = getString("accept");
    document.getElementById("selectSeatsInfo").innerText = getString("selectSeatsInfo");

    let selector = new Selector();
    selector.init();
}

class Selector {
    constructor() {
        this.presentation = null;
        this.locations = [];
        this.occupied = null;

        this.table = new TicketTable((ticket) => this.onDeleteFromTable(ticket));

        this.currentLocation = null;
        this.tickets = [];
        this.totalCost = 0.0;
    }

    async init() {
        this.updateAcceptButton();
        this.getPresentation();
        await this.getLocations();
        await this.getOccupied();
        this.table.createTable();
        this.updateTotalCost();
        document.getElementById("acceptButton").onclick = () => this.accept();
    }

    async accept() {
        let requestTickets = [];
        for (let item of this.tickets) {
            let t = {
                location: {
                    location: item.location
                },
                row: item.row,
                seat: item.seat,
                cost: item.cost
            }
            requestTickets.push(t);
        }
        let response = await doPost("/api/orders/", {
            presentation: {
                id: this.presentation.id
            },
            tickets: requestTickets
        });
        let data = await response.json();
        if (!data.code) {
            this.currentLocation = null;
            this.tickets = [];
            this.totalCost = 0.0;

            this.init();
        }
    }

    async getPresentation() {
        let params = new URLSearchParams(location.search);
        let response = await fetch("/api/presentations?id=" + params.get("id"));
        if (!checkCookies()) {
            return;
        }
        let data = await response.json();
        this.presentation = data.data;
        document.getElementById("presentationName").innerText = this.presentation.play.name;
        document.getElementById("presentationDate").innerText = this.presentation.date;
        document.getElementById("presentationAuthor").innerText = this.presentation.play.author.name;
        document.getElementById("presentationGenre").innerText = this.presentation.play.genre.name;
        document.getElementById("presentationDescription").innerText = this.presentation.play.description;
    }

    async getLocations() {
        let response = await fetch("/api/locations/");
        if (!checkCookies()) {
            return;
        }
        let data = await response.json();
        this.locations = data.data;
        let layout = document.getElementById("selectLayout");
        layout.innerHTML = "";
        layout.append(this.createSelect());
    }

    async getOccupied() {
        let params = new URLSearchParams(location.search);
        let response = await fetch("/api/locations/occupied?presentationId=" + params.get("id"));
        if (!checkCookies()) {
            return;
        }
        let data = await response.json();
        this.occupied = []
        for (let item of data.data) {
            this.occupied.push(this.createTicketFromResponse(item));
        }
        this.createSeatsTable();
    }

    createSelect() {
        let label = document.createElement("label");
        label.value = getString("selectLocation")
        let select = document.createElement("select");
        select.id = "selectLocation";
        for (let location of this.locations) {
            select.append(this.createOptionLocation(location.location));
        }
        this.currentLocation = this.locations[0];
        select.value = this.currentLocation.location;
        select.onchange = () => {
            let value = select.value;
            for (let location of this.locations) {
                if (value === location.location) {
                    this.currentLocation = location;
                    break;
                }
            }
            if (this.occupied !== null) {
                this.createSeatsTable();
            }
        };
        label.append(select);
        return label;
    }

    createOptionLocation(location) {
        let option = document.createElement("option");
        option.value = location;
        option.innerText = getString(location);
        return option;
    }

    createSeatsTable() {
        let rows = this.currentLocation.rows;
        let seats = this.currentLocation.seats;
        let table = document.createElement("table");
        let tbody = document.createElement("tbody");
        tbody.id = "selectTableBody";

        let numberRow = document.createElement("tr");
        numberRow.append(document.createElement("td"));
        for (let i = 1; i <= seats; i++) {
            let td = document.createElement("td");
            td.innerText = i + "";
            numberRow.append(td);
        }
        tbody.append(numberRow);
        for (let i = 1; i <= rows; i++) {
            let tr = document.createElement("tr");
            let firstColumn = document.createElement("td");
            firstColumn.innerText = i + "";
            tr.append(firstColumn);
            for (let j = 1; j <= seats; j++) {
                let ticket = this.createTicket(i, j);
                let td = document.createElement("td");
                td.id = `seat_${i}_${j}`;
                let occupiedSeat = false;
                for (let occupiedItem of this.occupied) {
                    if (ticket.equals(occupiedItem)) {
                        td.classList.add("occupied");
                        occupiedSeat = true;
                        break;
                    }
                }
                if (!occupiedSeat) {
                    let selectedSeat = false;
                    for (let item of this.tickets) {
                        if (ticket.equals(item)) {
                            td.classList.add("selected");
                            selectedSeat = true;
                            break;
                        }
                    }
                    if (selectedSeat) {
                        td.onclick = () => this.unselect(td, ticket, this.currentLocation.ticketCost);
                    } else {
                        td.onclick = () => this.select(td, ticket, this.currentLocation.ticketCost);
                    }
                }
                tr.append(td);
            }
            tbody.append(tr);
        }
        table.append(tbody);
        let layout = document.getElementById("seatsLayout");
        layout.innerHTML = "";
        layout.append(table);
    }

    select(td, ticket, cost) {
        this.totalCost += cost;
        td.onclick = () => this.unselect(td, ticket, cost);
        td.classList.add("selected");
        this.tickets.push(ticket);
        this.table.addRow(ticket, cost);
        this.updateTotalCost();
        this.updateAcceptButton();
    }

    unselect(td, ticket, cost) {
        this.totalCost -= cost;
        td.onclick = () => this.select(td, ticket, cost);
        td.classList.remove("selected");
        this.tickets.splice(this.findTicket(ticket), 1);
        this.table.deleteRow(ticket, false);
        this.updateTotalCost();
        this.updateAcceptButton();
    }

    findTicket(ticket) {
        for (let i = 0; i < this.tickets.length; i++) {
            if (ticket.equals(this.tickets[i])) {
                return i;
            }
        }
    }

    onDeleteFromTable(ticket) {
        if (ticket.location === this.currentLocation.location) {
            let td = document.getElementById(`seat_${ticket.row}_${ticket.seat}`);
            this.totalCost -= this.currentLocation.ticketCost;
            td.onclick = () => this.select(td, ticket, this.currentLocation.ticketCost);
            td.classList.remove("selected");
        } else {
            for (let location of this.locations) {
                if (location.location === ticket.location) {
                    this.totalCost -= location.ticketCost;
                    break;
                }
            }
        }
        this.updateTotalCost();
        this.updateAcceptButton();
        this.tickets.splice(this.findTicket(ticket), 1);
    }

    createTicket(row, seat) {
        return new Ticket(this.currentLocation.location, row, seat, this.currentLocation.totalCost);
    }

    createTicketFromResponse(item) {
        return new Ticket(item.location.location, item.row, item.seat)
    }

    updateTotalCost() {
        document.getElementById("totalCost").innerText = (Math.abs(this.totalCost)).toFixed(2);
    }

    updateAcceptButton() {
        document.getElementById("acceptButton").disabled = (this.tickets.length === 0);
    }
}


class TicketTable {
    constructor(onDelete) {
        this.tableId = "ticketTable";
        this.onDelete = onDelete;
    }

    getColumnNames() {
        return [
            getString("location"),
            getString("row"),
            getString("seat"),
            getString("cost")
        ];
    }

    get table() {
        return document.getElementById(this.tableId);
    }

    get content() {
        return this.table.querySelector(".content");
    }

    createTable() {
        let table = document.createElement("table");
        table.id = this.tableId;
        table.append(this.createThead());
        table.append(this.createTbody());
        let layout = document.getElementById("ticketsLayout");
        layout.innerHTML = "";
        layout.append(table);
    }

    createThead() {
        let thead = document.createElement("thead");
        let tr = document.createElement("tr");
        for (let label of this.getColumnNames()) {
            let td = document.createElement("td");
            td.innerText = label;
            tr.append(td);
        }
        tr.append(document.createElement("td"));
        thead.append(tr);
        return thead;
    }

    createTbody() {
        let tbody = document.createElement("tbody");
        tbody.className = "content";
        return tbody;
    }

    createDeleteButton(ticket) {
        let deleteButton = document.createElement("button");
        deleteButton.innerText = getString("delete");
        deleteButton.onclick = () => this.deleteRow(ticket);
        return deleteButton;
    }

    createRow(ticket, cost) {
        let tr = document.createElement("tr");
        tr.id = `item_${ticket.location}_${ticket.row}_${ticket.seat}`;
        let tdLocation = document.createElement("td");
        tdLocation.innerText = getString(ticket.location);
        let tdRow = document.createElement("td");
        tdRow.innerText = ticket.row;
        let tdSeat = document.createElement("td");
        tdSeat.innerText = ticket.seat;
        let tdCost = document.createElement("td");
        tdCost.innerText = cost;
        let tdButton = document.createElement("td");
        tdButton.append(this.createDeleteButton(ticket));
        tr.append(tdLocation);
        tr.append(tdRow);
        tr.append(tdSeat);
        tr.append(tdCost);
        tr.append(tdButton);
        return tr;
    }

    addRow(ticket, cost) {
        this.content.append(this.createRow(ticket, cost));
    }

    deleteRow(ticket, withCallback = true) {
        document.getElementById(`item_${ticket.location}_${ticket.row}_${ticket.seat}`).remove();
        if (withCallback) {
            this.onDelete(ticket);
        }
    }
}


class Ticket {
    constructor(location, row, seat, cost) {
        this.location = location;
        this.row = row;
        this.seat = seat;
        this.cost = cost;
    }

    equals(ticket) {
        return this.location === ticket.location
            && this.row === ticket.row
            && this.seat === ticket.seat;
    }
}