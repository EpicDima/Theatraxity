"use strict";

export default class KVStorage {
    static LOCALE_KEY = "locale";
    static AVAILABLE_LOCALES = ["ru", "en"];

    constructor() {
        this.checkLocale();
    }

    isValidLocale(locale) {
        return KVStorage.AVAILABLE_LOCALES.includes(locale);
    }

    getDefaultLocale() {
        return KVStorage.AVAILABLE_LOCALES[0];
    }

    setDefaultLocale() {
        this.setLocale(this.getDefaultLocale());
    }

    checkLocale() {
        let locale = localStorage.getItem(KVStorage.LOCALE_KEY);
        if (!this.isValidLocale(locale)) {
            this.setDefaultLocale();
        }
    }

    getLocale() {
        let locale = localStorage.getItem(KVStorage.LOCALE_KEY);
        if (!this.isValidLocale(locale)) {
            this.setDefaultLocale();
            return this.getDefaultLocale();
        }
        return locale;
    }

    setLocale(locale) {
        if (this.isValidLocale(locale)) {
            localStorage.setItem(KVStorage.LOCALE_KEY, locale);
            document.documentElement.lang = locale;
        }
    }
}