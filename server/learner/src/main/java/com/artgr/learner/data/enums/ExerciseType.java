package com.artgr.learner.data.enums;

// Constant names are lowercase to match the Postgres native enum values
// (01_schema.sql) and the JSON wire format (doc/api/swagger.yaml) exactly,
// so no name-mapping converter is needed in either direction.
public enum ExerciseType {
    en_ett,
    assemble,
    translate,
    base_form,
    produce_form,
    multi_select
}
