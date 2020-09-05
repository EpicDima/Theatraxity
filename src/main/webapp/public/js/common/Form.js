"use strict";

import getString from "./localization.js";
import {checkCookies} from "./common.js";

export default class Form {
    constructor(formName) {
        this.formName = formName;
    }

    setTable(table) {
        this.table = table;
    }

    getLabels() {
        return [];
    }

    getAttributes() {
        return [];
    }

    configureForm(form) {

    }

    get form() {
        return document.forms[this.formName];
    }

    get submitButton() {
        return this.form.querySelector("input[type=submit]");
    }

    get resetButton() {
        return this.form.querySelector("input[type=reset]");
    }

    get layout() {
        return document.getElementById(this.formName);
    }

    get errorField() {
        return this.layout.querySelector(".error");
    }

    create() {
        let layout = document.createElement("div");
        layout.className = "form"
        layout.id = this.formName;
        layout.append(this.createForm());
        let errorField = document.createElement("div");
        errorField.classList.add("error");
        layout.append(errorField);
        return layout;
    }

    createForm() {
        let form = document.createElement("form");
        form.name = this.formName;
        let title = document.createElement("div");
        title.className = "formTitle";
        title.innerText = getString("formTitle");
        form.append(title);
        form.append(this.createInput({name: "id", type: "hidden", value: 0}))
        form.append(this.createInput({name: "deleted", type: "hidden", value: false}))
        form.onsubmit = (e) => {
            e.preventDefault();
            this.table.addItem(this.getItemFromForm());
            this.errorField.innerHTML = "";
        };
        form.onreset = () => {
            this.errorField.innerHTML = "";
            this.submitButton.value = getString("add");
            form.onsubmit = (e) => {
                e.preventDefault();
                this.table.addItem(this.getItemFromForm());
                this.errorField.innerHTML = "";
            };
        };
        this.configureForm(form);
        let buttonsLayout = document.createElement("div");
        buttonsLayout.append(this.createResetButton(), this.createSubmitButton());
        form.append(buttonsLayout);
        return form;
    }

    createSubmitButton() {
        let submit = document.createElement("input");
        submit.type = "submit";
        submit.value = getString("submit");
        return submit;
    }

    createResetButton() {
        let reset = document.createElement("input");
        reset.type = "reset";
        reset.value = getString("reset");
        return reset;
    }

    createInputField(label, attributes) {
        let field = document.createElement("label");
        field.innerText = label;
        field.append(this.createInput(attributes));
        return field;
    }

    createInput(attributes) {
        let input = document.createElement("input");
        for (let key in attributes) {
            input.setAttribute(key, attributes[key]);
        }
        if (!attributes.type) {
            input.setAttribute("type", "text");
        }
        return input;
    }

    createSelectField(label, attributes, getValuesFunction) {
        let field = document.createElement("label");
        field.innerText = label;
        field.append(this.createSelect(attributes, getValuesFunction));
        return field;
    }

    createSelect(attributes, getValuesFunction) {
        let api = attributes.api;
        delete attributes.api;
        let select = document.createElement("select");
        for (let key in attributes) {
            select.setAttribute(key, attributes[key]);
        }
        if (api) {
            fetch(api)
                .then(response => response.json())
                .then(items => {
                    if (!items.code) {
                        for (let item of items.data) {
                            let option = document.createElement("option");
                            item = getValuesFunction(item);
                            option.value = item.key;
                            option.innerText = item.value;
                            select.append(option);
                        }
                    }
                });
        }
        return select;
    }

    changeItem(item) {
        this.setItemToForm(item);
        this.submitButton.value = getString("change");
        this.form.onsubmit = (e) => {
            e.preventDefault();
            this.table.changeItem(this.getItemFromForm());
            this.form.reset();
            this.errorField.innerHTML = "";
        };
    }

    setItemToForm(item) {

    }

    getItemFromForm() {
        return null;
    }
}