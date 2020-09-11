"use strict";

import TablePage from "../common/TablePage.js";
import Form from "../common/Form.js";
import Table from "../common/Table.js";
import FilterSort from "../common/FilterSort.js";
import {getContentLayout} from "../main.js";
import getString from "../common/localization.js";
import {checkCookies} from "../common/common.js";

export default function openPlayPage() {
    let layout = getContentLayout();
    layout.innerHTML = "";

    let table = new PlayTable();
    let filterSort = new PlayFilterSort();
    let form = new PlayForm();

    new TablePage(layout, table, filterSort, form).init();
}

class PlayForm extends Form {
    constructor() {
        super("playForm");
    }

    getLabels() {
        return [getString("playName"), getString("playDescription"),
            getString("author"), getString("genre")];
    }

    getAttributes() {
        return [
            {
                name: "name",
                placeholder: getString("inputTitle"),
                minlength: 3,
                maxlength: 100,
                required: true
            },
            {
                name: "description",
                placeholder: getString("inputDescription"),
                minlength: 5,
                maxlength: 1024,
                required: true
            },
            {
                name: "authorId",
                api: "/api/authors",
                required: true
            },
            {
                name: "genreId",
                api: "/api/genres",
                required: true
            }
        ];
    }

    configureForm(form) {
        let labels = this.getLabels();
        let attributes = this.getAttributes();
        form.append(this.createInputField(labels[0], attributes[0]));
        form.append(this.createInputField(labels[1], attributes[1]));
        form.append(this.createSelectField(labels[2], attributes[2], (item) => {
            return {key: item.id, value: item.name};
        }));
        form.append(this.createSelectField(labels[3], attributes[3], (item) => {
            return {key: item.id, value: item.name};
        }));
    }

    setItemToForm(item) {
        let form = this.form;
        form.id.value = item.id;
        form.name.value = item.name;
        form.description.value = item.description;
        form.authorId.value = item.author.id;
        form.genreId.value = item.genre.id;
        form.deleted.value = item.deleted;
    }

    getItemFromForm() {
        let form = this.form;
        return {
            id: form.id.value,
            name: form.name.value,
            description: form.description.value,
            author: {
                id: form.authorId.value
            },
            genre: {
                id: form.genreId.value
            },
            deleted: form.deleted.value
        };
    }
}


class PlayTable extends Table {
    constructor() {
        super("playTable", "/api/plays");
        this.moreDataNotAvailable = true;
    }

    getColumnNames() {
        return [
            getString("number"),
            getString("playName"),
            getString("author"),
            getString("genre"),
            getString("heDeleted")
        ];
    }

    createRow(item) {
        let tr = document.createElement("tr");
        tr.id = `item${item.id}`;
        let tdId = document.createElement("td");
        tdId.innerText = item.id;
        let tdName = document.createElement("td");
        tdName.innerText = item.name;
        let tdAuthor = document.createElement("td");
        tdAuthor.innerText = item.author.name
        let tdGenre = document.createElement("td");
        tdGenre.innerText = item.genre.name;
        let tdDeleted = document.createElement("td");
        tdDeleted.innerText = getString(item.deleted);
        tr.append(tdId);
        tr.append(tdName);
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


class PlayFilterSort extends FilterSort {

    getSortValues() {
        return [
            {
                key: "id",
                value: getString("number")
            },
            {
                key: "name",
                value: getString("playName")
            },
            {
                key: "author",
                value: getString("author")
            },
            {
                key: "genre",
                value: getString("genre")
            }
        ];
    }

    loadFilterSearchParams(params) {
        if (params.has("deleted")) {
            document.getElementById("deleted").checked = params.get("deleted");
        }
    }

    configureFilter(filter) {
        filter.append(this.createDeletedFilter());
        filter.append(this.createAuthorIdFilter());
        filter.append(this.createGenreIdFilter());
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