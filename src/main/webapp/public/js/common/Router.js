"use strict";

import getGuestRouter from "../guest/router.js";
import {UserRole} from "./constants.js";
import getClientRouter from "../client/router.js";
import getCourierRouter from "../courier/router.js";
import getManagerRouter from "../manager/router.js";
import getAdminRouter from "../admin/router.js";
import getString from "./localization.js";

export function refreshRouter(role = null) {
    let router = getRouter(role);
    window.router = router;
    router.init();
}

export function openPage(link) {
    history.pushState({}, "", link);
    window.router.resolve();
}

function getRouter(role = null) {
    if (role == null) {
        return getGuestRouter();
    }
    switch (role) {
        case UserRole.CLIENT:
            return getClientRouter();
        case UserRole.COURIER:
            return getCourierRouter();
        case UserRole.MANAGER:
            return getManagerRouter();
        case UserRole.ADMIN:
            return getAdminRouter();
    }
    return getGuestRouter();
}

export function goToMainPage() {
    openPage("/");
}


export default class Router {
    constructor() {
        this.routes = [];
        this.notFound = null;

        this.current = null;
    }

    init() {
        window.onpopstate = () => {
            this.resolve();
        }
    }

    addWithTitle(path, title_key, f) {
        if (!title_key) {
            title_key = "appName";
        }
        let title = getString(title_key);
        path.replaceAll("/", "");
        this.routes.push({"path": path, "title": title, "f": f});
        return this;
    }

    add(path, f) {
        return this.addWithTitle(path, null, f);
    }

    addNotFound(f) {
        this.notFound = f;
        return this;
    }

    resolve() {
        let path = location.pathname;
        path.replaceAll("/", "");
        for (let route of this.routes) {
            if (route.path === path) {
                this.current = path;
                document.title = route.title;
                route.f();
                return;
            }
        }
    }
}