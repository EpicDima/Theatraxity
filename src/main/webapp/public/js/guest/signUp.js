"use strict";

import getString from "../common/localization.js";
import {doPost} from "../common/api.js";
import {goToMainPage, refreshRouter} from "../common/Router.js";

export function configureSignUpPage() {
    document.getElementById("signUp").innerText = getString("signUp");
    document.getElementById("emailLabel").value = getString("email");
    document.forms["signUp"].email.placeholder = getString("inputEmail");
    document.getElementById("passwordLabel").value = getString("password");
    document.forms["signUp"].password.placeholder = getString("inputPassword");
    document.getElementById("repeatPasswordLabel").value = getString("repeatPassword");
    document.forms["signUp"].repeatPassword.placeholder = getString("inputPasswordRepeat");
    document.getElementById("signUpButton").value = getString("signUpButton");

    document.forms["signUp"].onsubmit = (e) => {
        e.preventDefault();
        signUp();
    };

    async function signUp() {
        let password = document.forms["signUp"].password.value;
        let repeatPassword = document.forms["signUp"].repeatPassword.value;
        if (password !== repeatPassword) {
            document.getElementById("error").innerHTML = getString("notSamePasswords");
            return;
        }
        document.getElementById("error").innerHTML = "";
        let response = await doPost("/api/users", {
            email: document.forms["signUp"].email.value,
            password: password
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
