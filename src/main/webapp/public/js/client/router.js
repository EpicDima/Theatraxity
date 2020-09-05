"use strict";

import openPresentationsPage from "./presentations.js";
import Router from "../common/Router.js";
import {getHeaderContentLayout, showInfoPage, showNotFoundPage} from "../main.js";
import {createHeaderLink, createSignOutButton, loadHtmlPage} from "../common/common.js";
import getString from "../common/localization.js";
import openOrdersPage from "./orders.js";
import {configureProfilePage} from "../common/profile.js";
import {configureOrderPage} from "./order.js";
import {configurePresentationPage} from "./presentation.js";

export default function getClientRouter() {
    createClientHeader();
    return new Router().addWithTitle("/profile", "profile", () => {
        loadHtmlPage("/html/profile.html", configureProfilePage);
    }).addWithTitle("/orders", "orders", () => {
        openOrdersPage();
    }).addWithTitle("/order", "order", () => {
        loadHtmlPage("/html/order.html", configureOrderPage);
    }).addWithTitle("/presentations", "presentations", () => {
        openPresentationsPage();
    }).addWithTitle("/presentation", "presentation", () => {
        loadHtmlPage("/html/presentation.html", configurePresentationPage);
    }).add("/", () => {
        showInfoPage();
    }).addNotFound(() => {
        showNotFoundPage();
    });
}


function createClientHeader() {
    let header = getHeaderContentLayout();
    header.innerHTML = "";
    header.append(
        createHeaderLink("/orders", getString("orders")),
        createHeaderLink("/presentations", getString("presentations")),
        createHeaderLink("/profile", getString("profile")),
        createSignOutButton()
    );
}