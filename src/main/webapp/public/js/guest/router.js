"use strict";

import Router from "../common/Router.js";
import {getHeaderContentLayout, showInfoPage, showNotFoundPage} from "../main.js";
import openPresentationsPage from "./presentations.js";
import {createHeaderLink, loadHtmlPage} from "../common/common.js";
import getString from "../common/localization.js";
import {configureSignUpPage} from "./signUp.js";
import {configureSignInPage} from "./signIn.js";

export default function getGuestRouter() {
    createGuestHeader();
    return new Router().addWithTitle("/signUp",  "signUp", async () => {
        loadHtmlPage("/html/signUp.html", configureSignUpPage);
    }).addWithTitle("/signIn", "signIn", async () => {
        loadHtmlPage("/html/signIn.html", configureSignInPage);
    }).addWithTitle("/presentations", "presentations", () => {
        openPresentationsPage();
    }).add("/", () => {
        showInfoPage();
    }).addNotFound(() => {
        showNotFoundPage();
    });
}

function createGuestHeader() {
    let header = getHeaderContentLayout();
    header.innerHTML = "";
    header.append(
        createHeaderLink("/presentations", getString("presentations")),
        createHeaderLink("/signUp", getString("signUp")),
        createHeaderLink("/signIn", getString("signIn"))
    );
}