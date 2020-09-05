"use strict";

import TablePage from "../common/TablePage.js";
import Form from "../common/Form.js";
import Table from "../common/Table.js";
import FilterSort from "../common/FilterSort.js";
import getString from "../common/localization.js";

export default function openAuthorPage() {
    let layout = document.getElementsByTagName("main")[0];
    layout.innerHTML = "";

    let table = new AuthorTable();
    let filterSort = new AuthorFilterSort();
    let form = new AuthorForm();

    new TablePage(layout, table, filterSort, form).init();
}

class AuthorForm extends Form {
    constructor() {
        super("authorForm");
    }

    getLabels() {
        return [getString("authorName")];
    }

    getAttributes() {
        return [
            {
                name: "name",
                placeholder: getString("inputName"),
                minlength: 2,
                maxlength: 100,
                required: true
            }
        ];
    }

    configureForm(form) {
        let labels = this.getLabels();
        let attributes = this.getAttributes();
        form.append(this.createInputField(labels[0], attributes[0]));
    }

    setItemToForm(item) {
        let form = this.form;
        form.id.value = item.id;
        form.name.value = item.name;
        form.deleted.value = item.deleted;
    }

    getItemFromForm() {
        let form = this.form;
        return {
            id: form.id.value,
            name: form.name.value,
            deleted: form.deleted.value
        };
    }
}


class AuthorTable extends Table {
    constructor() {
        super("authorTable", "/api/authors");
        this.moreDataNotAvailable = true;
    }

    getColumnNames() {
        return [
            getString("number"),
            getString("inputName"),
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
        let tdDeleted = document.createElement("td");
        tdDeleted.innerText = getString(item.deleted);
        tr.append(tdId);
        tr.append(tdName);
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


class AuthorFilterSort extends FilterSort {

    getSortValues() {
        return [
            {
                key: "id",
                value: getString("number")
            },
            {
                key: "name",
                value: getString("authorName")
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
}