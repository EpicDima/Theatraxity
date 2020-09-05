"use strict";

export async function doPost(url, item) {
    return doPostPutDelete(url, "POST", item);
}

export async function doPut(url, item) {
    return doPostPutDelete(url, "PUT", item);
}

export async function doDelete(url, item) {
    return doPostPutDelete(url, "DELETE", item);
}

async function doPostPutDelete(url, method, body) {
    return fetch(url, {
        method: method,
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    });
}