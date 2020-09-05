"use strict";

import KVStorage from "./common/KVStorage.js";
import {refreshRouter, openPage} from "./common/Router.js";
import {getCookie, loadHtmlPage} from "./common/common.js";
import {configureInfoPage} from "./common/info.js";
import {configureNotFoundPage} from "./common/notFound.js";
import getString from "./common/localization.js";

window.kvStorage = new KVStorage();

refreshRouter(getCookie("role"));
router.resolve();

document.getElementById("headerTitleLink").innerText = getString("appName");
document.getElementById("about").innerText = getString("about");
fillLanguageSelect();

export function getContentLayout() {
    return document.getElementsByTagName("main")[0];
}

export function getHeaderContentLayout() {
    return document.getElementById("headerContent");
}

export function showInfoPage() {
    loadHtmlPage("/html/info.html", configureInfoPage);
}

export function showNotFoundPage() {
    loadHtmlPage("/html/notFound.html", configureNotFoundPage);
}


document.addEventListener("click", (e) => {
    if (e.target.tagName === "A") {
        openPage(e.target.href);
        e.preventDefault();
    }
});

function fillLanguageSelect() {
    let select = document.getElementById("languageSelect");

    function createLanguageOption(locale) {
        let option = document.createElement("option");
        option.innerText = getString(locale);
        option.value = locale;
        return option;
    }

    for (let locale of KVStorage.AVAILABLE_LOCALES) {
        select.append(createLanguageOption(locale));
    }
    select.value = window.kvStorage.getLocale();
    select.onchange = () => {
        window.kvStorage.setLocale(select.value);
        location.reload();
    };
}



// a = {
//     "email": "admin@admin.com",
//     "password": "adminadmin"
// }
//
// fetch("http://localhost:8080/api/users/signIn", {
//     method: "POST",
//     headers: {
//         "Content-Type": "application/json"
//     },
//     body: JSON.stringify(a)
// })
