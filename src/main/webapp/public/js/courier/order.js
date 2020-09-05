"use strict";


import Table from "../common/Table.js";
import getString from "../common/localization.js";
import {checkCookies} from "../common/common.js";


export function configureOrderPage() {
    document.getElementById("orderNumberTitle").innerText = getString("orderNumber");
    document.getElementById("orderDateTitle").innerText = getString("orderDate");
    document.getElementById("orderStatusTitle").innerText = getString("status");
    document.getElementById("orderPlayNameTitle").innerText = getString("play");
    document.getElementById("orderPresentationDateTitle").innerText = getString("presentationDate");

    let table = new TicketTable();
    document.getElementById("tickets").append(table.createTable());
    table.download();
}


class TicketTable extends Table {
    constructor() {
        super("ticketTable", "/api/orders/");
        this.changeButtonEnabled = false;
        this.deleteButtonEnabled = false;
        this.restoreButtonEnabled = false;
    }

    getColumnNames() {
        return [
            getString("number"),
            getString("location"),
            getString("row"),
            getString("seat"),
            getString("cost")
        ];
    }

    createRow(item) {
        let tr = document.createElement("tr");
        tr.id = `item${item.id}`;
        let tdId = document.createElement("td");
        tdId.innerText = item.id;
        let tdLocation = document.createElement("td");
        tdLocation.innerText = getString(item.location.location);
        let tdRow = document.createElement("td");
        tdRow.innerText = item.row;
        let tdSeat = document.createElement("td");
        tdSeat.innerText = item.seat;
        let tdCost = document.createElement("td");
        tdCost.innerText = item.cost;
        tr.append(tdId);
        tr.append(tdLocation);
        tr.append(tdRow);
        tr.append(tdSeat);
        tr.append(tdCost);
        return tr;
    }

    async download() {
        this.loading = true;
        let response = await fetch(this.apiUrl + location.search);
        if (!checkCookies()) {
            return;
        }
        let items = await response.json();
        if (items.code) {
            this.error.innerText = getString(items.code);
        } else {
            this.deleteAll();
            document.getElementById("orderNumber").innerText = items.data.id;
            document.getElementById("orderDate").innerText = items.data.date;
            document.getElementById("orderStatus").innerText = getString(items.data.status);
            document.getElementById("orderPlayName").innerText = items.data.presentation.play.name;
            document.getElementById("orderPresentationDate").innerText = items.data.presentation.date;
            this.addAll(items.data.tickets);
            this.page = 1;
        }
        if (this.infinityScrolling) {
            this.moreDataNotAvailable = false;
        }
        this.loading = false;
    }
}