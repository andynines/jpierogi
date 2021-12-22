#!/usr/bin/env python3

import expression_class_description_parser as ecdp

import argparse
import pathlib
from typing import Dict, List

# TODO: Use common classes for binary operations, unary operations, etc.


def generate_class_fields(fields: Dict[str, str]) -> str:
    field_template = "private final {type} {name};"
    fields = [field_template.format(type=field_type, name=field_name) for field_name, field_type in fields.items()]
    return "\n\t".join(fields)


def generate_class_initializers(fields: Dict[str, str]) -> str:
    initializers = ["this.{name} = {name};".format(name=field_name) for field_name in fields.keys()]
    return "\n\t\t".join(initializers)


def generate_constructors(name: str, fields: Dict[str, str]) -> str:
    constructor_template = """\
\tpublic {class_name}({constructor_parameters}) {{
\t\tthis({constructor_arguments});
\t}}

\tpublic {class_name}({constructor_parameters_with_line_number}) {{
\t\tsuper(lineNumber);
\t\t{initializers}
\t}}"""
    constructor_parameters = [field_type + " " + field_name for field_name, field_type in fields.items()]
    return constructor_template.format(
        class_name=name,
        constructor_parameters=", ".join(constructor_parameters),
        constructor_arguments=", ".join(list(fields.keys()) + ["0"]),
        constructor_parameters_with_line_number=", ".join(constructor_parameters + ["int lineNumber"]),
        initializers=generate_class_initializers(fields)
    )


def generate_field_comparisons(fields: Dict[str, str]) -> str:
    if len(fields) == 0:
        return "true"
    comparison_template = "{name}.equals(other.{name})"
    return " && ".join([comparison_template.format(name=field_name) for field_name in fields.keys()])


def generate_getter(field_name: str, field_type: str) -> str:
    getter_template = """\
\tpublic {type} get{capitalized_name}() {{
\t\treturn {name};
\t}}
"""
    return getter_template.format(
        type=field_type,
        capitalized_name=field_name.capitalize(),
        name=field_name
    )


def generate_getters(fields: Dict[str, str]) -> str:
    return "\n".join([generate_getter(field_name, field_type) for field_name, field_type in fields.items()])


def generate_class_for_node(name: str, fields: Dict[str, str]) -> str:
    java_class_template = """\
package com.andrewsenin.pierogi.ast;

public class {class_name} extends Expression {{

\t{fields}

{constructors}

\t@Override
\tpublic <T> T accept(AstVisitor<T> astVisitor) {{
\t\treturn astVisitor.visit(this);
\t}}

\t@Override
\tpublic boolean equals(Expression expression) {{
\t\tif (!(expression instanceof {class_name})) {{
\t\t\treturn false;
\t\t}}
\t\t{class_name} other = ({class_name}) expression;
\t\treturn {field_comparisons};
\t}}

{getters}
}}
"""
    return java_class_template.format(
        class_name=name,
        fields=generate_class_fields(fields),
        constructors=generate_constructors(name, fields),
        initializers=generate_class_initializers(fields),
        field_comparisons=generate_field_comparisons(fields),
        getters=generate_getters(fields)
    )


def generate_ast_node_class_files(object_field_dict: Dict[str, Dict[str, str]], output_path: pathlib.Path) -> None:
    for node_name, node_fields in object_field_dict.items():
        java_source = generate_class_for_node(node_name, node_fields)
        with open(output_path.joinpath(node_name + ".java"), "w") as java_file:
            java_file.write(java_source)


def generate_visitors(field_names: List[str]) -> str:
    visitor_template = "public abstract T visit({name} {uncapitalized_name});"
    return "\n\t".join([
        visitor_template.format(
            name=field_name,
            uncapitalized_name=field_name[0].lower() + field_name[1:]
        )
        for field_name in field_names])


def generate_visitor_class_file(object_field_dict: Dict[str, Dict[str, str]], output_path: pathlib.Path) -> None:
    visitor_class_template = """\
package com.andrewsenin.pierogi.ast;

public abstract class AstVisitor<T> {{
\t{visitors}
}}
"""
    with open(output_path.joinpath("AstVisitor.java"), "w") as visitor_file:
        visitor_file.write(visitor_class_template.format(
            visitors=generate_visitors(list(object_field_dict.keys()))
        ))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("input_path", type=pathlib.Path, help="Path to input expression class description file")
    parser.add_argument("-o", "--output_path", type=pathlib.Path, required=False, default=".",
                        help="Path to directory of output .java files")
    args = parser.parse_args()

    object_field_dict = ecdp.make_class_dicts_from_file(args.input_path)
    generate_ast_node_class_files(object_field_dict, args.output_path)
    generate_visitor_class_file(object_field_dict, args.output_path)
