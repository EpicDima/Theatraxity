"use strict";

import getString from "../common/localization.js";
import {doPost} from "../common/api.js";
import {goToMainPage, refreshRouter} from "../common/Router.js";

export function configureSignInPage() {
    document.getElementById("signIn").innerText = getString("signIn");
    document.getElementById("emailLabel").value = getString("email");
    document.forms["signIn"].email.placeholder = getString("inputEmail");
    document.getElementById("passwordLabel").value = getString("password");
    document.forms["signIn"].password.placeholder = getString("inputPassword");
    document.getElementById("signInButton").value = getString("signInButton");

    document.forms["signIn"].onsubmit = (e) => {
        e.preventDefault();
        signIn();
    };

    async function signIn() {
        document.getElementById("error").innerHTML = "";
        let response = await doPost("/api/users/signIn", {
            email: document.forms["signIn"].email.value,
            password: document.forms["signIn"].password.value
        });
        let data = await response.json();
        if (data.code) {
            document.getElementById("error").innerHTML = getString(data.code);
        } else {
            refreshRouter(data.data.role);
            goToMainPage();
        }
    }
}

