"use strict";

import getString from "./localization.js";
import {doPost, doPut, doDelete} from "./api.js";
import {checkCookies} from "./common.js";

export default class Table {

    constructor(tableId, apiUrl) {
        this.tableId = tableId;
        this.apiUrl = apiUrl;

        this.changeButtonEnabled = true;
        this.deleteButtonEnabled = true;
        this.restoreButtonEnabled = true;

        this.infinityScrolling = false;
        this.page = 0;
        this.moreDataNotAvailable = false;
        this.loading = false;
    }

    setInfinityScroll() {
        this.infinityScrolling = true;
        window.onscroll = () => {
            if (this.table === null) {
                window.onscroll = null;
                return;
            }
            if ((window.pageYOffset + window.innerHeight) >= this.table.offsetHeight) {
                this.downloadMore();
            }
        };
    }

    getColumnNames() {
        return [];
    }

    setForm(form) {
        this.form = form;
    }

    setFilterSort(filterSort) {
        this.filterSort = filterSort;
    }

    get table() {
        return document.getElementById(this.tableId);
    }

    get content() {
        return this.table.querySelector(".content");
    }

    get error() {
        return document.getElementById("tableError");
    }

    createTable() {
        let layout = document.createElement("div");
        layout.id = "layout" + this.tableId;
        let table = document.createElement("table");
        table.id = this.tableId;
        table.append(this.createThead());
        table.append(this.createTbody());
        layout.append(table);
        let error = document.createElement("div");
        error.id = "tableError";
        layout.append(error);
        return layout;
    }

    createThead() {
        let thead = document.createElement("thead");
        let tr = document.createElement("tr");
        for (let label of this.getColumnNames()) {
            let td = document.createElement("td");
            td.innerText = label;
            tr.append(td);
        }
        if (this.buttonColumnExist()) {
            let buttonsTd = document.createElement("td");
            tr.append(buttonsTd);
        }
        thead.append(tr);
        return thead;
    }

    buttonColumnExist() {
        return this.changeButtonEnabled || this.deleteButtonEnabled
            || this.restoreButtonEnabled;
    }

    createTbody() {
        let tbody = document.createElement("tbody");
        tbody.className = "content";
        return tbody;
    }

    createRow(item) {
        return null;
    }

    addRow(item) {
        this.content.append(this.createRow(item));
    }

    replaceRow(item) {
        document.getElementById(`item${item.id}`).replaceWith(this.createRow(item));
    }

    deleteRow(item) {
        document.getElementById(`item${item.id}`).remove();
    }

    addAll(items) {
        for (let item of items) {
            this.addRow(item);
        }
    }

    deleteAll() {
        this.content.innerHTML = "";
    }

    async download() {
        this.loading = true;
        this.error.innerText = "";
        let response = await fetch(this.apiUrl + location.search);
        let items = await response.json();
        if (items.code) {
            this.error.innerText = getString(items.code);
        } else {
            this.deleteAll();
            this.addAll(items.data);
            this.page = 1;
        }
        if (this.infinityScrolling) {
            this.moreDataNotAvailable = false;
        }
        this.loading = false;
    }

    async downloadMore() {
        if (this.moreDataNotAvailable || this.loading) {
            return;
        }
        this.loading = true;
        this.error.innerText = "";
        let params = location.search;
        if (params.length === 0) {
            params = "?";
        } else {
            params += "&";
        }
        params += "page=" + this.page;
        let response = await fetch(this.apiUrl + params);
        let items = await response.json();
        if (items.code) {
            this.error.innerText = getString(items.code);
            this.loading = true;
        } else {
            if (items.data.length === 0) {
                this.moreDataNotAvailable = true;
                return;
            }
            this.addAll(items.data);
            this.page++;
        }
        this.loading = false;
    }

    async addItem(item) {
        let response = await doPost(this.apiUrl, item);
        item = await response.json();
        if (item.code) {
            this.form.errorField.innerText = getString(item.code);
        } else {
            this.addRow(item.data);
            this.form.form.reset();
        }
    }

    async changeItem(item) {
        let response = await doPut(this.apiUrl, item);
        item = await response.json();
        if (item.code) {
            this.form.errorField.innerText = getString(item.code);
        } else {
            this.replaceRow(item.data);
            this.form.form.reset();
        }
    }

    async deleteItem(item) {
        let response = await doDelete(this.apiUrl, item);
        let deleteItem = await response.json();
        if (deleteItem.code) {
            this.form.errorField.innerText = getString(deleteItem.code);
        } else {
            this.deleteRow(item);
        }
    }

    async restoreItem(item) {
        let response = await doPost(this.apiUrl + "/restore", item);
        item = await response.json();
        if (item.code) {
            this.form.errorField.innerText = getString(item.code);
        } else {
            this.deleteRow(item.data);
        }
    }

    createDeleteButton(item) {
        let deleteButton = document.createElement("button");
        deleteButton.innerText = getString("delete");
        deleteButton.onclick = (e) => {
            e.stopPropagation();
            this.deleteItem(item);
        }
        return deleteButton;
    }

    createChangeButton(item) {
        let changeButton = document.createElement("button");
        changeButton.innerText = getString("change");
        changeButton.onclick = (e) => {
            e.stopPropagation();
            this.form.changeItem(item);
        }
        return changeButton;
    }

    createRestoreButton(item) {
        let deleteButton = document.createElement("button");
        deleteButton.innerText = getString("restore");
        deleteButton.onclick = (e) => {
            e.stopPropagation();
            this.restoreItem(item);
        }
        return deleteButton;
    }

    createButton(text, onclick) {
        let button = document.createElement("button");
        button.innerText = text;
        button.onclick = onclick;
        return button;
    }
}