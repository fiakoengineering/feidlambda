// REPEAT_ARRAY_BY_ROW & REPEAT_ARRAY_BY_COLUMN --> REPEAT_ARRAY
REPEAT_ARRAY = LAMBDA(array, [num_repeat], [by_row],
    LET(
        by_row, IF(ISOMITTED(by_row), TRUE, by_row),
        num_repeat, IF(ISOMITTED(num_repeat), 2, num_repeat),
        IF(
            by_row,
            REPEAT_ARRAY_BY_ROW(array, num_repeat),
            REPEAT_ARRAY_BY_COLUMN(array, num_repeat)
        )
    )
);

// REPEAT_ARRAY_BY_ROW --> REPEAT_ARRAY_BY_ROW
REPEAT_ARRAY_BY_ROW = LAMBDA(array, [num_repeat],
    LET(
        num_repeat, IF(ISOMITTED(num_repeat), 2, num_repeat),
        IF(
            num_repeat = 1,
            array,
            LET(
                next_repeat, num_repeat - 1,
                VSTACK(REPEAT_ARRAY_BY_ROW(array, next_repeat), array)
            )
        )
    )
);

// REPEAT_ARRAY_BY_COLUMN --> REPEAT_ARRAY_BY_COLUMN
REPEAT_ARRAY_BY_COLUMN = LAMBDA(array, [num_repeat],
    LET(
        num_repeat, IF(ISOMITTED(num_repeat), 2, num_repeat),
        IF(
            num_repeat = 1,
            array,
            LET(
                next_repeat, num_repeat - 1,
                HSTACK(REPEAT_ARRAY_BY_COLUMN(array, next_repeat), array)
            )
        )
    )
);
