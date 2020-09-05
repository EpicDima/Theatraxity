"use strict";

import getString from "./localization.js";

export function configureNotFoundPage() {
    document.getElementById("notFound").innerText = getString("notFoundPage");
}
