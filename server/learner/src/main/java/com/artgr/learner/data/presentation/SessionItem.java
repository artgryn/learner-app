package com.artgr.learner.data.presentation;

// A session's `items` array is an ordered mix of IntroduceItem (word
// introduction, new words only) and ExerciseItem (graded), discriminated by
// `itemType`. An IntroduceItem for a lexemeId always precedes that word's
// first ExerciseItem for the same lexemeId. See API.md "Session items".
public sealed interface SessionItem permits IntroduceItem, ExerciseItem {

    String itemId();

    Long lexemeId();
}
