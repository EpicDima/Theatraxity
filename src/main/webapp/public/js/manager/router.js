"use strict";

import openPlayPage from "./plays.js";
import openGenrePage from "./genres.js";
import openAuthorPage from "./authors.js";
import openPresentationsPage from "./presentations.js";
import Router from "../common/Router.js";
import {getHeaderContentLayout, showInfoPage, showNotFoundPage} from "../main.js";
import {createHeaderLink, createSignOutButton, loadHtmlPage} from "../common/common.js";
import getString from "../common/localization.js";
import openUsersPage from "./users.js";
import openOrdersPage from "./orders.js";
import {configureProfilePage} from "../common/profile.js";
import {configureOrderPage} from "./order.js";

export default function getManagerRouter() {
    createManagerHeader();
    return new Router().addWithTitle("/profile", "profile", () => {
        loadHtmlPage("/html/profile.html", configureProfilePage);
    }).addWithTitle("/users", "users", () => {
        openUsersPage();
    }).addWithTitle("/orders", "orders", () => {
        openOrdersPage();
    }).addWithTitle("/order", "order", () => {
        loadHtmlPage("/html/order.html", configureOrderPage);
    }).addWithTitle("/plays", "plays", () => {
        openPlayPage();
    }).addWithTitle("/genres", "genres", () => {
        openGenrePage();
    }).addWithTitle("/authors", "authors", () => {
        openAuthorPage();
    }).addWithTitle("/presentations", "presentations", () => {
        openPresentationsPage();
    }).add("/", () => {
        showInfoPage();
    }).addNotFound(() => {
        showNotFoundPage();
    });
}


function createManagerHeader() {
    let header = getHeaderContentLayout();
    header.innerHTML = "";
    header.append(
        createHeaderLink("/authors", getString("authors")),
        createHeaderLink("/genres", getString("genres")),
        createHeaderLink("/plays", getString("plays")),
        createHeaderLink("/orders", getString("orders")),
        createHeaderLink("/presentations", getString("presentations")),
        createHeaderLink("/users", getString("users")),
        createHeaderLink("/profile", getString("profile")),
        createSignOutButton()
    );
}