"use strict";

export default class TablePage {
    constructor(layout, table, filterSort, form) {
        this.layout = layout;
        this.table = table;
        this.filterSort = filterSort;
        this.form = form;

        this.table.setForm(this.form);
        this.table.setFilterSort(this.filterSort);
        this.filterSort.setTable(this.table);
        if (this.form) {
            this.form.setTable(this.table);
        }
    }

    init() {
        if (this.form) {
            this.layout.append(this.form.create());
        }
        this.layout.append(this.filterSort.createLayout());
        this.layout.append(this.table.createTable());

        this.filterSort.loadSearchParams();
        this.table.download();
    }
}