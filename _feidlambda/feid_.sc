/*
feidlambda v0.4.0 - LOGIC / UTILITIES FUNCTIONS BY FIAKO ENGINEERING
OFFICIAL GIST (feidlambda v0.4.x): 
    https://gist.github.com/taruma/b4df638ecb7af48ab63691951481d6b2
REPOSITORY: 
    https://github.com/fiakoenjiniring/feidlambda
CONTRIBUTOR: @taruma, @iingLK
TESTED: Microsoft Excel 365 v2304
*/

// BATAS MAKSMIMUM LAYAR EDITOR -------------------------------------------#

/* 
---- APPLY ----
*/

// NONE --> APPLY_COLUMN
APPLY_COLUMN = LAMBDA(array, index_vector, LAMBDA_FUNCTION,
    LET(
        index_vector, SORT(index_vector),
        selected_array, CHOOSECOLS(array, index_vector),
        applied_array, LAMBDA_FUNCTION(selected_array),
        sequence_vector, SEQUENCE(COLUMNS(array)),
        logical_vector, BYROW(
            sequence_vector,
            LAMBDA(row, OR(row = index_vector))
        ),
        scan_vector, SCAN(
            0,
            logical_vector,
            LAMBDA(acc, curr, IF(curr, acc + 1, acc))
        ),
        position_vector, scan_vector + COLUMNS(array),
        all_array, HSTACK(array, applied_array),
        selected_vector, MAP(
            logical_vector,
            sequence_vector,
            position_vector,
            LAMBDA(logical_el, seq_el, pos_el,
                IF(logical_el, pos_el, seq_el)
            )
        ),
        CHOOSECOLS(all_array, selected_vector)
    )
);

/*
---- FILTER ----
*/

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

/*
---- GET ----
*/

// NONE --> GET_INDEX_2D
GET_INDEX_2D = LAMBDA(lookup_value, array, [return_as_order],
    LET(
        return_as_order, IF(
            ISOMITTED(return_as_order),
            FALSE,
            return_as_order
        ),
        nrows, ROWS(array),
        ncols, COLUMNS(array),
        size, nrows * ncols,
        array_flatten, TOCOL(array, , TRUE),
        index_sequence, SEQUENCE(nrows, ncols, 1, 1),
        rows_sequence, MAKEARRAY(nrows, ncols, LAMBDA(x, y, x)),
        columns_sequence, MAKEARRAY(nrows, ncols, LAMBDA(x, y, y)),
        rows_flatten, TOCOL(rows_sequence, , TRUE),
        columns_flatten, TOCOL(columns_sequence, , TRUE),
        index_flatten, TOCOL(index_sequence, , TRUE),
        lookup_table, HSTACK(index_flatten, rows_flatten, columns_flatten),
        lookup_result, FILTER(lookup_table, array_flatten = lookup_value),
        IF(return_as_order, CHOOSECOLS(lookup_result, 1), lookup_result)
    )
);

// _RECURSIVE_LOOKUP --> _RECURSIVE_LOOKUP
_RECURSIVE_LOOKUP = LAMBDA(
    ntry,
    lookup_value,
    lookup_vector,
    return_array,
    [if_not_found],
    [match_mode],
    [search_mode],
    LET(
        lookup_value, TOCOL(lookup_value),
        LET(
            selected_value, VALUE(
                ARRAYTOTEXT(CHOOSEROWS(lookup_value, ntry))
            ),
            result, XLOOKUP(
                selected_value,
                lookup_vector,
                return_array,
                if_not_found,
                match_mode,
                search_mode
            ),
            IF(
                ntry = 1,
                result,
                VSTACK(
                    _RECURSIVE_LOOKUP(
                        ntry - 1,
                        lookup_value,
                        lookup_vector,
                        return_array,
                        if_not_found,
                        match_mode,
                        search_mode
                    ),
                    result
                )
            )
        )
    )
);

// _RECURSIVE_LOOKUP --> GET_XLOOKUP
GET_XLOOKUP = LAMBDA(
    lookup_value,
    lookup_vector,
    return_array,
    [if_not_found],
    [match_mode],
    [search_mode],
    LET(
        lookup_value, TOCOL(lookup_value),
        ntry, ROWS(lookup_value),
        _RECURSIVE_LOOKUP(
            ntry,
            lookup_value,
            lookup_vector,
            return_array,
            if_not_found,
            match_mode,
            search_mode
        )
    )
);

/*
---- IS ----
*/

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

/*
---- MAKE ----
*/

// _RECURSIVE_MAKE_SEQUENCE --> _RECURSIVE_MAKE_SEQUENCE
_RECURSIVE_MAKE_SEQUENCE = LAMBDA(
    start_vector,
    end_vector,
    ntry,
    [stack_horizontally],
    LET(
        seq_start, INDEX(start_vector, ntry),
        seq_end, INDEX(end_vector, ntry),
        stack_horizontally, IF(
            ISOMITTED(stack_horizontally),
            FALSE,
            stack_horizontally
        ),
        IF(
            ntry = 1,
            SEQUENCE(seq_end - seq_start + 1, , seq_start),
            LET(
                next_try, ntry - 1,
                results, SEQUENCE(seq_end - seq_start + 1, , seq_start),
                IF(
                    stack_horizontally,
                    HSTACK(
                        _RECURSIVE_MAKE_SEQUENCE(
                            start_vector,
                            end_vector,
                            next_try,
                            stack_horizontally
                        ),
                        results
                    ),
                    VSTACK(
                        _RECURSIVE_MAKE_SEQUENCE(
                            start_vector,
                            end_vector,
                            next_try,
                            stack_horizontally
                        ),
                        results
                    )
                )
            )
        )
    )
);

// _RECURSIVE_MAKE_SEQUENCE --> MAKE_SEQUENCE_FROM_VECTOR
MAKE_SEQUENCE_FROM_VECTOR = LAMBDA(
    start_vector,
    end_vector,
    [stack_horizontally],
    _RECURSIVE_MAKE_SEQUENCE(
        start_vector,
        end_vector,
        ROWS(start_vector),
        stack_horizontally
    )
);

/*
---- REPEAT ----
*/

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

/*
---- RESHAPE ----
*/

// NONE --> RESHAPE_BY_COLUMNS
RESHAPE_BY_COLUMNS = LAMBDA(array, [num_split],
    LET(
        num_split, IF(ISOMITTED(num_split), 2, num_split),
        ncols, COLUMNS(array),
        nrows, ROWS(array),
        IF(
            MOD(ncols, num_split) = 0,
            LET(
                divider, ncols / num_split,
                divider_sequence, CHOOSEROWS(
                    SEQUENCE(1, divider),
                    SEQUENCE(num_split, , 1, 0)
                ),
                divider_flatten, TOCOL(divider_sequence, , TRUE),
                divider_repeat, CHOOSEROWS(
                    TOROW(divider_flatten),
                    SEQUENCE(nrows, , 1, 0)
                ),
                divider_repeat_col, TOCOL(divider_repeat),
                array_flatten, TOCOL(array),
                array_sorted, SORTBY(array_flatten, divider_repeat_col),
                WRAPROWS(array_sorted, num_split)
            ),
            NA()
        )
    )
);

/*
---- ROTATE ----
*/

// NONE --> ROTATE_VECTOR
ROTATE_VECTOR = LAMBDA(vector, num_rotation, [as_column_vector],
    LET(
        vector, TOCOL(vector),
        rotated_array, IFS(
            OR(
                num_rotation = 0,
                num_rotation >= ROWS(vector),
                num_rotation <= -ROWS(vector)
            ),
            vector,
            num_rotation > 0,
            VSTACK(DROP(vector, num_rotation), TAKE(vector, num_rotation)),
            num_rotation < 0,
            VSTACK(TAKE(vector, num_rotation), DROP(vector, num_rotation))
        ),
        as_column_vector, IF(ISOMITTED(as_column_vector), FALSE, TRUE),
        IF(as_column_vector, TOROW(rotated_array), TOCOL(rotated_array))
    )
);

// ROTATE_VECTOR --> ROTATE_ARRAY
ROTATE_ARRAY = LAMBDA(array, num_rotation, [rotate_columns],
    LET(
        rotate_columns, IF(ISOMITTED(rotate_columns), TRUE, FALSE),
        nrows, ROWS(array),
        ncols, COLUMNS(array),
        seqrows, SEQUENCE(nrows),
        seqcols, SEQUENCE(1, ncols),
        results, IF(
            rotate_columns,
            CHOOSECOLS(array, ROTATE_VECTOR(seqcols, num_rotation, TRUE)),
            CHOOSEROWS(array, ROTATE_VECTOR(seqrows, num_rotation, FALSE))
        ),
        results
    )
);

/*
---- SWAP ----
*/

// NONE --> SWAP_COLUMNS
SWAP_COLUMNS = LAMBDA(array, [from_index], [to_index],
    LET(
        ncols, COLUMNS(array),
        from_index, IF(ISOMITTED(from_index), 1, from_index),
        to_index, IF(ISOMITTED(to_index), -1, to_index),
        from_value, IF(from_index < 0, from_index + ncols + 1, from_index),
        to_value, IF(to_index < 0, to_index + ncols + 1, to_index),
        column_sequence, SEQUENCE(1, COLUMNS(array)),
        from_logical, column_sequence = from_value,
        to_logical, column_sequence = to_value,
        replace_from, IF(from_logical, to_value, column_sequence),
        replace_to, IF(to_logical, from_value, replace_from),
        CHOOSECOLS(array, replace_to)
    )
);

// NONE --> SWAP_ROWS
SWAP_ROWS = LAMBDA(array, [from_index], [to_index],
    LET(
        nrows, ROWS(array),
        from_index, IF(ISOMITTED(from_index), 1, from_index),
        to_index, IF(ISOMITTED(to_index), -1, to_index),
        from_value, IF(from_index < 0, from_index + nrows + 1, from_index),
        to_value, IF(to_index < 0, to_index + nrows + 1, to_index),
        row_sequence, SEQUENCE(ROWS(array)),
        from_logical, row_sequence = from_value,
        to_logical, row_sequence = to_value,
        replace_from, IF(from_logical, to_value, row_sequence),
        replace_to, IF(to_logical, from_value, replace_from),
        CHOOSEROWS(array, replace_to)
    )
);

/*
---- TEXT ----
*/

// _RECURSIVE_TEXT_SPLIT --> _RECURSIVE_TEXT_SPLIT
_RECURSIVE_TEXT_SPLIT = LAMBDA(
    text_vector,
    ntry,
    col_delimiter,
    [row_delimiter],
    [ignore_empty],
    [match_mode],
    [pad_with],
    LET(
        text_vector, TOCOL(text_vector),
        selected_row, ARRAYTOTEXT(INDEX(text_vector, ntry)),
        IF(
            ntry = 1,
            TEXTSPLIT(
                selected_row,
                col_delimiter,
                row_delimiter,
                ignore_empty,
                match_mode,
                pad_with
            ),
            LET(
                next_try, ntry - 1,
                results, TEXTSPLIT(
                    selected_row,
                    col_delimiter,
                    row_delimiter,
                    ignore_empty,
                    match_mode,
                    pad_with
                ),
                VSTACK(
                    _RECURSIVE_TEXT_SPLIT(
                        text_vector,
                        next_try,
                        col_delimiter,
                        row_delimiter,
                        ignore_empty,
                        match_mode,
                        pad_with
                    ),
                    results
                )
            )
        )
    )
);

// _RECURSIVE_TEXT_SPLIT --> TEXT_SPLIT_VECTOR
TEXT_SPLIT_VECTOR = LAMBDA(
    text_vector,
    [col_delimiter],
    [row_delimiter],
    [ignore_empty],
    [match_mode],
    [pad_with],
    [replace_na],
    LET(
        nrows, ROWS(text_vector),
        col_delimiter, IF(ISOMITTED(col_delimiter), " ", col_delimiter),
        replace_na, IF(ISOMITTED(replace_na), NA(), replace_na),
        pad_with, IF(ISOMITTED(pad_with), "", pad_with),
        result, _RECURSIVE_TEXT_SPLIT(
            text_vector,
            nrows,
            col_delimiter,
            row_delimiter,
            ignore_empty,
            match_mode,
            pad_with
        ),
        IFERROR(result, replace_na)
    )
);

/*
MIT License

Copyright (c) 2022-2023 PT. FIAKO ENJINIRING INDONESIA

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/