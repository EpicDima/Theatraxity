"use strict";

export const UserRole = Object.freeze({
    CLIENT: "CLIENT",
    COURIER: "COURIER",
    MANAGER: "MANAGER",
    ADMIN: "ADMIN"
});


export const Page = Object.freeze({
    MAIN: "/",
    SIGN_UP: "/signUp",
    SIGN_IN: "/signIn",
    PROFILE: "/profile",
    USERS: "/users",
    ORDERS: "/orders",
    MAKE_ORDER: "/make_order",
    PLAYS: "/plays",
    GENRES: "/genres",
    AUTHORS: "/authors",
    PRESENTATIONS: "/presentations"
});


export const OrderStatus = Object.freeze({
    UNPAID: "UNPAID",
    PAID: "PAID",
    CANCELLED: "CANCELLED"
});


export const Location = Object.freeze({
    BALCONY_LEFT: "BALCONY_LEFT",
    BALCONY_MIDDLE: "BALCONY_MIDDLE",
    BALCONY_RIGHT: "BALCONY_RIGHT",
    MEZZANINE_LEFT: "MEZZANINE_LEFT",
    MEZZANINE_MIDDLE: "MEZZANINE_MIDDLE",
    MEZZANINE_RIGHT: "MEZZANINE_RIGHT",
    AMPHITHEATRE: "AMPHITHEATRE",
    BENOIR_LEFT: "BENOIR_LEFT",
    BENOIR_RIGHT: "BENOIR_RIGHT",
    PARTERRE: "PARTERRE"
});