// NONE --> FILTER_DROP_ROWS
FILTER_DROP_ROWS = LAMBDA(array, row_index,
    LET(
        row_index, TOCOL(row_index),
        row_index_clean, FILTER(row_index, NOT(ISBLANK(row_index))),
        nrows, ROWS(array),
        row_sequence, SEQUENCE(nrows),
        selected_row, BYROW(
            row_sequence,
            LAMBDA(each_row, OR(each_row = row_index_clean))
        ),
        FILTER(array, NOT(selected_row))
    )
);

// NONE --> FILTER_DROP_COLUMNS
FILTER_DROP_COLUMNS = LAMBDA(array, column_index,
    LET(
        column_index, TOROW(column_index),
        column_index_clean, FILTER(
            column_index,
            NOT(ISBLANK(column_index))
        ),
        ncols, COLUMNS(array),
        col_sequence, SEQUENCE(1, ncols),
        selected_col, BYCOL(
            col_sequence,
            LAMBDA(each_col, OR(each_col = column_index_clean))
        ),
        FILTER(array, NOT(selected_col))
    )
);

// NONE --> FILTER_FUNC_COLUMN
FILTER_FUNC_COLUMN = LAMBDA(
    array,
    [column_index],
    [with_label],
    [label_col],
    [function],
    [label_function],
    [take_first_only],
    LET(
        take_first_only, IF(
            ISOMITTED(take_first_only),
            FALSE,
            take_first_only
        ),
        column_index, IF(ISOMITTED(column_index), 1, column_index),
        label_col, IF(ISOMITTED(label_col), column_index, label_col),
        with_label, IF(ISOMITTED(with_label), FALSE, with_label),
        function, IF(ISOMITTED(function), LAMBDA(x, MAX(x)), function),
        label_function, IF(
            ISOMITTED(label_function),
            "func",
            label_function
        ),
        selected_vector, CHOOSECOLS(array, column_index),
        func_value, function(selected_vector),
        selected_logical, selected_vector = func_value,
        array_filter, FILTER(array, selected_logical),
        array_func, IF(
            take_first_only,
            TAKE(array_filter, 1),
            array_filter
        ),
        label, MAKEARRAY(
            ROWS(array_func),
            1,
            LAMBDA(x, y, CONCAT(label_col, "_", label_function))
        ),
        IF(with_label, HSTACK(label, array_func), array_func)
    )
);

// FILTER_FUNC_COLUMN --> FILTER_MINMAX_COLUMN
FILTER_MINMAX_COLUMN = LAMBDA(
    array,
    [column_index],
    [with_label],
    [label_col],
    [take_first_only],
    LET(
        func_1, LAMBDA(x, MIN(x)),
        label_func_1, "min",
        func_2, LAMBDA(x, MAX(x)),
        label_func_2, "max",
        func1_result, FILTER_FUNC_COLUMN(
            array,
            column_index,
            with_label,
            label_col,
            func_1,
            label_func_1,
            take_first_only
        ),
        func2_result, FILTER_FUNC_COLUMN(
            array,
            column_index,
            with_label,
            label_col,
            func_2,
            label_func_2,
            take_first_only
        ),
        VSTACK(func1_result, func2_result)
    )
);

// FILTER_MINMAX_COLUMN --> _RECURSIVE_FILTER_MINMAX
// _RECURSIVE_FILTER_MINMAX --> _RECURSIVE_FILTER_MINMAX
_RECURSIVE_FILTER_MINMAX = LAMBDA(
    array,
    ntry,
    [ignore_first_column],
    [with_label],
    [label_vector],
    [take_first_only],
    LET(
        ignore_first_column, IF(
            ISOMITTED(ignore_first_column),
            FALSE,
            ignore_first_column
        ),
        stop_col, IF(ignore_first_column, 2, 1),
        label_vector, IF(
            ISOMITTED(label_vector),
            SEQUENCE(1, COLUMNS(array)),
            label_vector
        ),
        new_label, IF(
            stop_col = 2,
            HSTACK({" "}, label_vector),
            label_vector
        ),
        label_col, CHOOSECOLS(new_label, ntry),
        IF(
            ntry = stop_col,
            FILTER_MINMAX_COLUMN(
                array,
                ntry,
                with_label,
                label_col,
                take_first_only
            ),
            LET(
                results, FILTER_MINMAX_COLUMN(
                    array,
                    ntry,
                    with_label,
                    label_col,
                    take_first_only
                ),
                next_try, ntry - 1,
                VSTACK(
                    _RECURSIVE_FILTER_MINMAX(
                        array,
                        next_try,
                        ignore_first_column,
                        with_label,
                        label_vector,
                        take_first_only
                    ),
                    results
                )
            )
        )
    )
);

// _RECURSIVE_FILTER_MINMAX --> FILTER_MINMAX_ARRAY
FILTER_MINMAX_ARRAY = LAMBDA(
    array,
    [ignore_first_column],
    [with_label],
    [label_vector],
    [take_first_only],
    _RECURSIVE_FILTER_MINMAX(
        array,
        COLUMNS(array),
        ignore_first_column,
        with_label,
        label_vector,
        take_first_only
    )
);
