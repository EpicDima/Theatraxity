"use strict";

import getString from "./localization.js";

export default class FilterSort {

    setTable(table) {
        this.table = table;
    }

    get layout() {
        return document.getElementById("filterSort");
    }

    get sort() {
        return document.getElementById("sort");
    }

    get desc() {
        return document.getElementById("desc");
    }

    getSortValues() {
        return [];
    }

    configureFilter(filter) {

    }

    loadSearchParams() {
        let params = this.getSearchParams();
        if (params.has("sort")) {
            this.sort.value = params.get("sort");
        }
        if (params.has("desc")) {
            this.desc.checked = params.get("desc");
        }
        this.loadFilterSearchParams(params);
    }

    loadFilterSearchParams(params) {

    }

    configureLayout(layout) {

    }

    createLayout() {
        let layout = document.createElement("div");
        layout.id = "filterSort";
        let filter = document.createElement("div");
        filter.className = "filter";
        filter.innerText = getString("filter");
        layout.append(filter);
        layout.append(this.createFilter());
        let sort = document.createElement("div");
        sort.className = "sort";
        sort.innerText = getString("sort");
        layout.append(sort);
        layout.append(this.createSort());
        layout.append(this.createDesc());
        this.configureLayout(layout);
        return layout;
    }

    createFilter() {
        let filter = document.createElement("div");
        this.configureFilter(filter);
        return filter;
    }

    createSort() {
        let label = document.createElement("label");
        label.innerText = getString("sort");
        let sort = document.createElement("select");
        sort.id = "sort";
        let option = document.createElement("option");
        option.value = "0";
        option.innerText = getString("notSpecified");
        sort.append(option);
        for (let item of this.getSortValues()) {
            let option = document.createElement("option");
            option.value = item.key;
            option.innerText = item.value;
            sort.append(option);
        }
        sort.onchange = () => {
            let searchParams = this.getSearchParams();
            let value = sort.options[sort.selectedIndex].value;
            if (value === "0") {
                searchParams.delete("sort");
            } else {
                searchParams.set("sort", value);
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(sort);
        return label;
    }

    createDesc() {
        let label = document.createElement("label");
        label.innerText = getString("desc");
        let desc = document.createElement("input");
        desc.type = "checkbox";
        desc.id = "desc";
        desc.onchange = () => {
            let searchParams = this.getSearchParams();
            if (desc.checked) {
                searchParams.set("desc", "true");
            } else {
                searchParams.delete("desc");
            }
            this.setSearchParams(searchParams);
            this.table.download();
        };
        label.append(desc);
        return label;
    }

    getSearchParams() {
        return new URLSearchParams(location.search);
    }

    setSearchParams(searchParams) {
        history.pushState({}, "", `?${searchParams}`);
    }
}