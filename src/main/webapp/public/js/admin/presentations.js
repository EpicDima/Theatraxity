"use strict";

import TablePage from "../common/TablePage.js";
import Form from "../common/Form.js";
import Table from "../common/Table.js";
import FilterSort from "../common/FilterSort.js";
import {getContentLayout} from "../main.js";
import {openPage} from "../common/Router.js";
import getString from "../common/localization.js";
import {checkCookies} from "../common/common.js";

export default function openPresentationsPage() {
    let layout = getContentLayout();
    layout.innerHTML = "";

    let table = new PresentationTable();
    let filterSort = new PresentationFilterSort();
    let form = new PresentationForm();

    new TablePage(layout, table, filterSort, form).init();
    table.setInfinityScroll();
}

class PresentationForm extends Form {
    constructor() {
        super("presentationsForm");
    }

    getLabels() {
        return [getString("presentationDate"), getString("play")];
    }

    getAttributes() {
        let minDate = new Date()
        minDate.setDate(minDate.getDate() + 1)
        return [
            {
                name: "date",
                type: "date",
                placeholder: getString("inputDate"),
                min: minDate.toISOString().split("T")[0],
                required: true
            },
            {
                name: "playId",
                api: "/api/plays",
                required: true
            }
        ];
    }

    configureForm(form) {
        let labels = this.getLabels();
        let attributes = this.getAttributes();
        form.append(this.createInputField(labels[0], attributes[0]));
        form.append(this.createSelectField(labels[1], attributes[1], (item) => {
            return {key: item.id, value: item.name};
        }));
    }

    setItemToForm(item) {
        let form = this.form;
        form.id.value = item.id;
        form.date.value = item.date;
        form.playId.value = item.play.id;
        form.deleted.value = item.deleted;
    }

    getItemFromForm() {
        let form = this.form;
        return {
            id: form.id.value,
            date: form.date.value,
            play: {
                id: form.playId.value
            },
            deleted: form.deleted.value
        };
    }
}


class PresentationTable extends Table {
    constructor() {
        super("presentationTable", "/api/presentations");
    }

    getColumnNames() {
        return [
            getString("number"),
            getString("playName"),
            getString("date"),
            getString("author"),
            getString("genre"),
            getString("itDeleted")
        ];
    }

    createRow(item) {
        let tr = document.createElement("tr");
        tr.id = `item${item.id}`;
        let tdId = document.createElement("td");
        tdId.innerText = item.id;
        let tdName = document.createElement("td");
        tdName.innerText = item.play.name;
        let tdDate = document.createElement("td");
        tdDate.innerText = item.date
        let tdAuthor = document.createElement("td");
        tdAuthor.innerText = item.play.author.name
        let tdGenre = document.createElement("td");
        tdGenre.innerText = item.play.genre.name;
        let tdDeleted = document.createElement("td");
        tdDeleted.innerText = getString(item.deleted);
        tr.append(tdId);
        tr.append(tdName);
        tr.append(tdDate);
        tr.append(tdAuthor);
        tr.append(tdGenre);
        tr.append(tdDeleted);
        let buttonsTd = document.createElement("td");
        if (this.buttonColumnExist()) {
            let changeButton = this.createChangeButton(item);
            let secondButton;
            if (item.deleted) {
                secondButton = this.createRestoreButton(item);
            } else {
                secondButton = this.createDeleteButton(item);
            }
            buttonsTd.append(changeButton, secondButton);
        }
        tr.append(buttonsTd);
        return tr;
    }
}


class PresentationFilterSort extends FilterSort {

    getSortValues() {
        return [
            {
                key: "id",
                value: getString("number")
            },
            {
                key: "play",
                value: getString("play")
            },
            {
                key: "author",
                value: getString("author")
            },
            {
                key: "genre",
                value: getString("genre")
            },
            {
                key: "date",
                value: getString("date")
            }
        ];
    }

    loadFilterSearchParams(params) {
        if (params.has("deleted")) {
            document.getElementById("deleted").checked = params.get("deleted");
        }
        if (params.has("begin")) {
            document.getElementById("begin").value = params.get("begin");
        }
        if (params.has("end")) {
            document.getElementById("end").value = params.get("end");
        }
        if (params.has("playId")) {
            document.getElementById("playId").value = params.get("playId");
        }
        if (params.has("authorId")) {
            document.getElementById("authorId").value = params.get("authorId");
        }
        if (params.has("genreId")) {
            document.getElementById("genreId").value = params.get("genreId");
        }
    }

    configureFilter(filter) {
        filter.append(this.createDeletedFilter());
        filter.append(this.createBeginDateFilter());
        filter.append(this.createEndDateFilter());
        filter.append(this.createPlayIdFilter());
        filter.append(this.createAuthorIdFilter());
        filter.append(this.createGenreIdFilter());
    }

    createDeletedFilter() {
        let label = document.createElement("label");
        label.innerText = getString("itDeleted");
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

    createBeginDateFilter() {
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

    createEndDateFilter() {
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

    createPlayIdFilter() {
        let label = document.createElement("label");
        label.innerText = getString("play");
        let select = document.createElement("select");
        select.id = "playId";
        select.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = select.options[select.selectedIndex].value;
            if (value === "0") {
                searchParams.delete("playId");
            } else {
                searchParams.set("playId", value);
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        fetch("api/plays")
            .then(response => response.json())
            .then(items => {
                if (!checkCookies()) {
                    return;
                }
                if (!items.code) {
                    let option = document.createElement("option");
                    option.value = "0";
                    option.innerText = getString("notSpecified");
                    select.append(option);
                    for (let item of items.data) {
                        let option = document.createElement("option");
                        option.value = item.id;
                        option.innerText = item.name;
                        select.append(option);
                    }
                    let params = this.getSearchParams();
                    if (params.has("playId")) {
                        document.getElementById("playId").value = params.get("playId");
                    }
                }
            });
        label.append(select);
        return label;
    }


    createAuthorIdFilter() {
        let label = document.createElement("label");
        label.innerText = getString("author");
        let select = document.createElement("select");
        select.id = "authorId";
        select.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = select.options[select.selectedIndex].value;
                if (value === "0") {
                searchParams.delete("authorId");
            } else {
                searchParams.set("authorId", value);
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        fetch("api/authors")
            .then(response => response.json())
            .then(items => {
                if (!checkCookies()) {
                    return;
                }
                if (!items.code) {
                    let option = document.createElement("option");
                    option.value = "0";
                    option.innerText = getString("notSpecified");
                    select.append(option);
                    for (let item of items.data) {
                        let option = document.createElement("option");
                        option.value = item.id;
                        option.innerText = item.name;
                        select.append(option);
                    }
                    let params = this.getSearchParams();
                    if (params.has("authorId")) {
                        document.getElementById("authorId").value = params.get("authorId");
                    }
                }
            });
        label.append(select);
        return label;
    }

    createGenreIdFilter() {
        let label = document.createElement("label");
        label.innerText = getString("genre");
        let select = document.createElement("select");
        select.id = "genreId";
        select.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = select.options[select.selectedIndex].value;
            if (value === "0") {
                searchParams.delete("genreId");
            } else {
                searchParams.set("genreId", value);
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        fetch("api/genres")
            .then(response => response.json())
            .then(items => {
                if (!checkCookies()) {
                    return;
                }
                if (!items.code) {
                    let option = document.createElement("option");
                    option.value = "0";
                    option.innerText = getString("notSpecified");
                    select.append(option);
                    for (let item of items.data) {
                        let option = document.createElement("option");
                        option.value = item.id;
                        option.innerText = item.name;
                        select.append(option);
                    }
                    let params = this.getSearchParams();
                    if (params.has("genreId")) {
                        document.getElementById("deleted").value = params.get("genreId");
                    }
                }
            });
        label.append(select);
        return label;
    }
}