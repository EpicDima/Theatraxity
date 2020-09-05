"use strict";

import {getContentLayout} from "../main.js";
import {goToMainPage, refreshRouter} from "./Router.js";
import getString from "./localization.js";

export function getCookie(name) {
    let matches = document.cookie.match(new RegExp(
        "(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, '\\$1') + "=([^;]*)"
    ));
    return matches ? decodeURIComponent(matches[1]) : undefined;
}

export async function loadHtmlPage(url, configurator) {
    let response = await fetch(url);
    if (response.ok) {
        getContentLayout().innerHTML = await response.text();
        configurator();
    }
}

export function createSignOutButton() {
    let div = document.createElement("div");
    div.id = "signOut";
    let button = document.createElement("button");
    button.id = "signOutButton";
    button.innerText = getString("signOut");
    button.onclick = () => signOut();
    div.append(button);
    return div;
}

export async function signOut() {
    let response = await fetch("api/users/signOut", {
        method: "POST"
    });
    refreshRouter(null);
    goToMainPage();
}

export function createHeaderLink(href, text) {
    let div = document.createElement("div");
    let a = document.createElement("a");
    a.href = href;
    a.innerText = text;
    div.classList.add("headerLink");
    div.append(a);
    return div;
}

export function createOptionRole(role) {
    let option = document.createElement("option");
    option.value = role;
    option.innerText = getString(role);
    return option;
}

export function checkCookies() {
    if (!getCookie("email") || !getCookie("role") || !getCookie("id")) {
        refreshRouter(null);
        goToMainPage();
        return false;
    }
    return true;
}