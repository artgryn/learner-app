package com.artgr.learner.data.enums;

// 'free' | 'paid' - billing tier (doc/api/swagger.yaml AccountStatus). No
// billing logic yet; not user-editable via PATCH /me.
public enum AccountStatus {
    free,
    paid
}
