"use strict";

import {doDelete, doPut} from "./api.js";
import getString from "./localization.js";
import {goToMainPage, refreshRouter} from "./Router.js";
import {showNotFoundPage} from "../main.js";
import {checkCookies} from "./common.js";

export async function configureProfilePage() {
    document.getElementById("profileTitle").innerText = getString("profileTitle");
    document.getElementById("emailLabel").value = getString("email");
    document.forms["profile"].email.placeholder = getString("inputEmail");
    document.getElementById("passwordLabel").value = getString("password");
    document.forms["profile"].password.placeholder = getString("inputPassword");
    document.getElementById("newPasswordLabel").value = getString("newPassword");
    document.forms["profile"].newPassword.placeholder = getString("inputNewPassword");

    let response = await fetch("/api/users/profile");
    if (!checkCookies()) {
        return;
    }
    let data = await response.json();
    if (data.code) {
        showNotFoundPage();
        return;
    }
    let email = data.data.email
    let role = data.data.role

    document.forms["profile"].email.value = email;

    document.getElementById("roleTitle").value = getString("role");
    document.getElementById("role").innerText = getString(role);

    let deleteButton = document.getElementById("deleteButton");
    deleteButton.innerText = getString("deleteProfile");
    deleteButton.onclick = async () => {
        let response = await doDelete("api/users/profile");
        if (response.ok) {
            refreshRouter(null);
            goToMainPage();
        }
    };

    let changeButton = document.getElementById("changeButton");
    changeButton.innerText = getString("profileChangeButtonDefault");
    let change = () => {
        changeButton.innerText = getString("profileChangeButtonActive");
        document.getElementById("error").innerText = "";
        document.getElementById("passwordRow").classList.remove("hide");
        document.getElementById("newPasswordRow").classList.remove("hide");
        document.forms["profile"].email.readOnly = false;

        changeButton.onclick = async () => {
            let user = {};
            let newEmail = document.forms["profile"].email.value;
            if (email !== newEmail && document.forms["profile"].email.validity.valid) {
                user.email = newEmail;
            }
            if (document.forms["profile"].password.validity.valid
                && document.forms["profile"].newPassword.validity.valid) {
                user.password = document.forms["profile"].password.value;
                user.newPassword = document.forms["profile"].newPassword.value;
            }

            if (user.email || user.password) {
                let response = await doPut("api/users/profile", user);
                let data = await response.json();
                if (data.code) {
                    document.getElementById("error").innerText = getString(data.code);
                } else {
                    reset();
                    document.forms["profile"].email.value = data.data.email;
                }
            } else {
                reset();
                document.forms["profile"].email.value = email;
            }
            changeButton.onclick = () => change();
        };
    };
    changeButton.onclick = () => change();

    function reset() {
        changeButton.innerText = getString("profileChangeButtonDefault");
        document.getElementById("passwordRow").classList.add("hide");
        document.getElementById("newPasswordRow").classList.add("hide");
        document.forms["profile"].email.readOnly = true;
        document.forms["profile"].reset();
    }
}
