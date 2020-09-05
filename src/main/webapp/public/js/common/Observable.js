"use strict";

export default class Observable {

    _proxies = [];

    _observers = [];

    constructor() {
        this.addProxy(this);
    }

    addProxy(obj) {
        this._proxies.push(this.createProxy(obj));
    }

    createProxy(obj) {
        return new Proxy(obj, {
            set(target, property, value) {
                if (Reflect.set(...arguments)) {
                    target._observers.forEach(observer => observer(target, property, value));
                    return true;
                }
                return false;
            },
        })
    }

    observe(observer) {
        this._observers.push(observer);
    }

    removeObserver(observer) {
        const index = this._observers.indexOf(observer);
        if (index !== -1) {
            this._observers = this._observers.splice(index, 1);
        }
    }

    clearObservers() {
        this._observers = [];
    }
}
