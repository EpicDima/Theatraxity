"use strict";

import TablePage from "../common/TablePage.js";
import Table from "../common/Table.js";
import FilterSort from "../common/FilterSort.js";
import {getContentLayout} from "../main.js";
import getString from "../common/localization.js";
import {OrderStatus} from "../common/constants.js";
import {doDelete, doPut} from "../common/api.js";
import {openPage} from "../common/Router.js";

export default function openOrdersPage() {
    let layout = getContentLayout();
    layout.innerHTML = "";

    let table = new OrderTable();
    let filterSort = new OrderFilterSort();

    new TablePage(layout, table, filterSort, null).init();
    table.setInfinityScroll();
}


class OrderTable extends Table {
    constructor() {
        super("orderTable", "/api/orders");
    }

    getColumnNames() {
        return [
            getString("number"),
            getString("orderDate"),
            getString("play"),
            getString("date"),
            getString("buyer"),
            getString("courier"),
            getString("status"),
            getString("heDeleted")
        ];
    }

    createRow(item) {
        let tr = document.createElement("tr");
        tr.id = `item${item.id}`;
        let tdId = document.createElement("td");
        tdId.innerText = item.id;
        let tdDate = document.createElement("td");
        tdDate.innerText = item.date;
        let tdPresentation = document.createElement("td");
        tdPresentation.innerText = item.presentation.play.name;
        let tdPresentationDate = document.createElement("td");
        tdPresentationDate.innerText = item.presentation.date;
        let tdBuyer = document.createElement("td");
        tdBuyer.innerText = item.buyer.email;
        let tdCourier = document.createElement("td");
        tdCourier.innerText = item.courier.email;
        let tdStatus = document.createElement("td");
        tdStatus.innerText = getString(item.status);
        let tdDeleted = document.createElement("td");
        tdDeleted.innerText = getString(item.deleted);
        tr.append(tdId);
        tr.append(tdDate);
        tr.append(tdPresentation);
        tr.append(tdPresentationDate);
        tr.append(tdBuyer);
        tr.append(tdCourier);
        tr.append(tdStatus);
        tr.append(tdDeleted);
        let buttonsTd = document.createElement("td");
        if (item.status === OrderStatus.UNPAID) {
            let cancelButton = this.createButton(getString("cancel"), async () => {
                let response = await doDelete("api/orders", item);
                let from = await response.json();
                if (from.data) {
                    this.replaceRow(item);
                }
            });
            let confirmButton = this.createButton(getString("confirmPayment"), async () => {
                let response = await doPut("api/orders", item);
                let from = await response.json();
                if (from.data) {
                    this.replaceRow(item);
                }
            });
            buttonsTd.append(cancelButton, confirmButton);
        }
        tr.append(buttonsTd);
        tr.onclick = () => {
            openPage(`order?id=${item.id}`);
        };
        return tr;
    }
}


class OrderFilterSort extends FilterSort {

    getSortValues() {
        return [
            {
                key: "id",
                value: getString("number")
            },
            {
                key: "date",
                value: getString("date")
            },
            {
                key: "status",
                value: getString("status")
            }
        ];
    }

    loadFilterSearchParams(params) {
        if (params.has("deleted")) {
            document.getElementById("deleted").checked = params.get("deleted");
        }
        if (params.has("userId")) {
            document.getElementById("userId").value = params.get("userId");
        }
        if (params.has("userEmail")) {
            document.getElementById("userEmail").value = params.get("userEmail");
        }
        if (params.has("presentationId")) {
            document.getElementById("presentationId").value = params.get("presentationId");
        }
        if (params.has("presentationDate")) {
            document.getElementById("presentationDate").value = params.get("presentationDate");
        }
        if (params.has("begin")) {
            document.getElementById("begin").value = params.get("begin");
        }
        if (params.has("end")) {
            document.getElementById("end").value = params.get("end");
        }
        if (params.has("status")) {
            document.getElementById("status").value = params.get("status");
        }
    }

    configureFilter(filter) {
        filter.append(this.createUserIdFilter());
        filter.append(this.createUserEmailFilter());
        filter.append(this.createPresentationIdFilter());
        filter.append(this.createPresentationDateFilter());
        filter.append(this.createBeginFilter());
        filter.append(this.createEndFilter());
        filter.append(this.createStatusFilter());
        filter.append(this.createDeletedFilter());
    }

    createUserIdFilter() {
        let label = document.createElement("label");
        label.innerText = getString("userNumber");
        let inputElement = document.createElement("input");
        inputElement.placeholder = getString("inputNumber");
        inputElement.type = "number";
        inputElement.id = "userId";
        inputElement.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = inputElement.value;
            if (value) {
                searchParams.set("userId", value);
            } else {
                searchParams.delete("userId");
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(inputElement);
        return label;
    }

    createUserEmailFilter() {
        let label = document.createElement("label");
        label.innerText = getString("userEmail");
        let inputElement = document.createElement("input");
        inputElement.type = "email";
        inputElement.placeholder = getString("inputEmail");
        inputElement.minlength = "6";
        inputElement.maxlength = "64";
        inputElement.id = "userEmail";
        inputElement.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = inputElement.value;
            if (value) {
                searchParams.set("userEmail", value);
            } else {
                searchParams.delete("userEmail");
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(inputElement);
        return label;
    }

    createPresentationIdFilter() {
        let label = document.createElement("label");
        label.innerText = getString("presentationNumber");
        let inputElement = document.createElement("input");
        inputElement.type = "number";
        inputElement.placeholder = getString("inputNumber");
        inputElement.id = "presentationId";
        inputElement.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = inputElement.value;
            if (value) {
                searchParams.set("presentationId", value);
            } else {
                searchParams.delete("presentationId");
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(inputElement);
        return label;
    }

    createPresentationDateFilter() {
        let label = document.createElement("label");
        label.innerText = getString("presentationDate");
        let inputElement = document.createElement("input");
        inputElement.type = "date";
        inputElement.id = "presentationDate";
        inputElement.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = inputElement.value;
            if (value) {
                searchParams.set("presentationDate", new Date(value).toISOString().split("T")[0]);
            } else {
                searchParams.delete("presentationDate");
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(inputElement);
        return label;
    }

    createBeginFilter() {
        let label = document.createElement("label");
        label.innerText = getString("beginDate");
        let inputElement = document.createElement("input");
        inputElement.type = "date";
        inputElement.id = "begin";
        inputElement.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = inputElement.value;
            if (value) {
                searchParams.set("begin", new Date(value).toISOString().split("T")[0]);
            } else {
                searchParams.delete("begin");
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(inputElement);
        return label;
    }

    createEndFilter() {
        let label = document.createElement("label");
        label.innerText = getString("endDate");
        let inputElement = document.createElement("input");
        inputElement.type = "date";
        inputElement.id = "end";
        inputElement.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = inputElement.value;
            if (value) {
                searchParams.set("end", new Date(value).toISOString().split("T")[0]);
            } else {
                searchParams.delete("end");
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(inputElement);
        return label;
    }

    createDeletedFilter() {
        let label = document.createElement("label");
        label.innerText = getString("heDeleted");
        let deleted = document.createElement("input");
        deleted.type = "checkbox";
        deleted.id = "deleted";
        deleted.onchange = () => {
            let searchParams = this.getSearchParams();
            if (deleted.checked) {
                searchParams.set("deleted", "true");
            } else {
                searchParams.delete("deleted");
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(deleted);
        return label;
    }

    createStatusFilter() {
        let label = document.createElement("label");
        label.innerText = getString("status");
        let select = document.createElement("select");
        select.id = "status";
        select.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = select.options[select.selectedIndex].value;
            if (value === "0") {
                searchParams.delete("status");
            } else {
                searchParams.set("status", value);
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        let option = document.createElement("option");
        option.value = "0";
        option.innerText = getString("notSpecified");
        select.append(option,
            this.createOptionStatus(OrderStatus.UNPAID),
            this.createOptionStatus(OrderStatus.PAID),
            this.createOptionStatus(OrderStatus.CANCELLED));
        label.append(select);
        return label;
    }

    createOptionStatus(status) {
        let option = document.createElement("option");
        option.value = status;
        option.innerText = getString(status);
        return option;
    }
}