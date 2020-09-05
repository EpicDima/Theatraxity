"use strict";

import getString from "./localization.js";

export function configureInfoPage() {
    document.getElementById("info").innerText = getString("info");
}
