"use strict";

import TablePage from "../common/TablePage.js";
import Form from "../common/Form.js";
import Table from "../common/Table.js";
import FilterSort from "../common/FilterSort.js";
import {getContentLayout} from "../main.js";
import {UserRole} from "../common/constants.js";
import {createOptionRole} from "../common/common.js";
import {openPage} from "../common/Router.js";
import getString from "../common/localization.js";

export default function openUsersPage() {
    let layout = getContentLayout();
    layout.innerHTML = "";

    let table = new UserTable();
    let filterSort = new UserFilterSort();
    let form = new UserForm();

    new TablePage(layout, table, filterSort, form).init();
    table.setInfinityScroll();
}

class UserForm extends Form {
    constructor() {
        super("userForm");
    }

    getLabels() {
        return [getString("email"), getString("password"),
            getString("role")];
    }

    getAttributes() {
        return [
            {
                name: "email",
                type: "email",
                placeholder: getString("inputEmail"),
                minlength: "6",
                maxlength: "64"
            },
            {
                name: "password",
                type: "password",
                placeholder: getString("inputPassword"),
                minlength: "8",
                maxlength: "64"
            },
            {
                name: "role"
            }
        ];
    }

    configureForm(form) {
        let labels = this.getLabels();
        let attributes = this.getAttributes();
        form.append(this.createInputField(labels[0], attributes[0]));
        form.append(this.createInputField(labels[1], attributes[1]));
        let roleSelect = this.createSelectField(labels[2], attributes[2], null);
        roleSelect.firstElementChild.append(createOptionRole(UserRole.CLIENT),
            createOptionRole(UserRole.COURIER),
            createOptionRole(UserRole.MANAGER),
            createOptionRole(UserRole.ADMIN));
        form.append(roleSelect);
    }

    setItemToForm(item) {
        let form = this.form;
        form.id.value = item.id;
        form.email.value = item.email;
        form.deleted.value = item.deleted;
        form.role.value = item.role;
    }

    getItemFromForm() {
        let form = this.form;
        return {
            id: form.id.value,
            email: form.email.value,
            password: form.password.value,
            deleted: form.deleted.value,
            role: form.role.value
        };
    }
}


class UserTable extends Table {
    constructor() {
        super("userTable", "/api/users");
    }

    getColumnNames() {
        return [
            getString("number"),
            getString("email"),
            getString("role"),
            getString("heDeleted")
        ];
    }

    createRow(item) {
        let tr = document.createElement("tr");
        tr.id = `item${item.id}`;
        let tdId = document.createElement("td");
        tdId.innerText = item.id;
        let tdEmail = document.createElement("td");
        tdEmail.innerText = item.email;
        let tdRole = document.createElement("td");
        tdRole.innerText = getString(item.role);
        let tdDeleted = document.createElement("td");
        tdDeleted.innerText = getString(item.deleted);
        tr.append(tdId);
        tr.append(tdEmail);
        tr.append(tdRole);
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
        tr.onclick = () => {
            if (item.role === UserRole.CLIENT || item.role === UserRole.COURIER) {
                openPage(`orders?userId=${item.id}`);
            }
        };
        return tr;
    }
}


class UserFilterSort extends FilterSort {

    getSortValues() {
        return [
            {
                key: "id",
                value: getString("number")
            },
            {
                key: "email",
                value: getString("email")
            }
        ];
    }

    loadFilterSearchParams(params) {
        if (params.has("deleted")) {
            document.getElementById("deleted").checked = params.get("deleted");
        }
        if (params.has("role")) {
            document.getElementById("role").value = params.get("role");
        }
    }

    configureFilter(filter) {
        filter.append(this.createDeletedFilter());
        filter.append(this.createRoleFilter());
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

    createRoleFilter() {
        let label = document.createElement("label");
        label.innerText = getString("role");
        let select = document.createElement("select");
        select.id = "role";
        select.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = select.options[select.selectedIndex].value;
            if (value === "0") {
                searchParams.delete("role");
            } else {
                searchParams.set("role", value);
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        let option = document.createElement("option");
        option.value = "0";
        option.innerText = getString("notSpecified");
        select.append(option, createOptionRole(UserRole.CLIENT),
            createOptionRole(UserRole.COURIER),
            createOptionRole(UserRole.MANAGER),
            createOptionRole(UserRole.ADMIN));
        label.append(select);
        return label;
    }
}