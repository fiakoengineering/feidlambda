// NONE --> IS_ALL_IN_LOOKUP_VECTOR
IS_ALL_IN_VECTOR = LAMBDA(lookup_vector, array,
    LET(
        lookup_vector, TOCOL(lookup_vector),
        MAP(
            array,
            LAMBDA(element,
                OR(BYROW(lookup_vector, LAMBDA(lookup, element = lookup)))
            )
        )
    )
);

// NONE --> IS_COLS_EQUAL_LOOKUP_VECTOR
IS_COLS_EQUAL_VECTOR = LAMBDA(lookup_vector, array,
    LET(
        lookup_vector, TOROW(lookup_vector),
        ncols_vector, COLUMNS(lookup_vector),
        ncols_array, COLUMNS(array),
        nrows_array, ROWS(array),
        IF(
            ncols_array = ncols_vector,
            LET(
                repeat_array, CHOOSEROWS(
                    lookup_vector,
                    SEQUENCE(nrows_array, , 1, 0)
                ),
                MAP(array, repeat_array, LAMBDA(x, y, x = y))
            ),
            "N/A"
        )
    )
);

// IS_COLS_EQUAL_LOOKUP_VECTOR --> IS_ALL_COLS_EQUAL_LOOKUP_VECTOR
IS_ALL_COLS_EQUAL_VECTOR = LAMBDA(lookup_vector, array, [logical_function],
    LET(
        logical_function, IF(
            ISOMITTED(logical_function),
            LAMBDA(x, OR(x)),
            logical_function
        ),
        array_boolean, IS_COLS_EQUAL_VECTOR(lookup_vector, array),
        BYROW(array_boolean, LAMBDA(each_row, logical_function(each_row)))
    )
);

// NONE --> IS_ROWS_LOGICAL
IS_ROWS_LOGICAL = LAMBDA(logical_array, [logical_function],
    LET(
        logical_function, IF(
            ISOMITTED(logical_function),
            LAMBDA(x, OR(x)),
            logical_function
        ),
        BYROW(logical_array, LAMBDA(each_row, logical_function(each_row)))
    )
);

// NONE --> IS_COLUMNS_LOGICAL
IS_COLUMNS_LOGICAL = LAMBDA(logical_array, [logical_function],
    LET(
        logical_function, IF(
            ISOMITTED(logical_function),
            LAMBDA(x, OR(x)),
            logical_function
        ),
        BYCOL(logical_array, LAMBDA(each_col, logical_function(each_col)))
    )
);
