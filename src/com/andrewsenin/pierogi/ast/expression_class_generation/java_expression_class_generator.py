#!/usr/bin/env python3

import argparse
import pathlib
from typing import Dict, List

# TODO: lots of code cleanup. Consolidate generation of uniques, bases, and derived into one template
# TODO: reorder function definitions


def generate_class_fields(fields: Dict[str, str]) -> str:
    field_template = "\tprotected final {type} {name};"
    fields = [field_template.format(type=field_type, name=field_name) for field_name, field_type in fields.items()]
    return "\n".join(fields)


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


def generate_derived_constructors(name: str, fields: Dict[str, str]) -> str:
    constructor_template = """\
\tpublic {class_name}({constructor_parameters}) {{
\t\tthis({constructor_arguments});
\t}}

\tpublic {class_name}({constructor_parameters_with_line_number}) {{
\t\tsuper({super_constructor_arguments});
\t}}"""
    constructor_parameters = [field_type + " " + field_name for field_name, field_type in fields.items()]
    return constructor_template.format(
        class_name=name,
        constructor_parameters=", ".join(constructor_parameters),
        constructor_arguments=", ".join(list(fields.keys()) + ["0"]),
        super_constructor_arguments=", ".join(list(fields.keys()) + ["lineNumber"]),
        constructor_parameters_with_line_number=", ".join(constructor_parameters + ["int lineNumber"])
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
\t}}"""
    return getter_template.format(
        type=field_type,
        capitalized_name=field_name.capitalize(),
        name=field_name
    )


def generate_getters(fields: Dict[str, str]) -> str:
    return "\n\n".join([generate_getter(field_name, field_type) for field_name, field_type in fields.items()])


def generate_visitors(class_names: List[str]) -> str:
    visitor_template = "T visit({name} {uncapitalized_name});"
    return "\n\t".join([
        visitor_template.format(
            name=class_name,
            uncapitalized_name=class_name[0].lower() + class_name[1:]
        )
        for class_name in class_names])


def generate_visitor_class_file(class_names: List[str], output_path: pathlib.Path) -> None:
    visitor_class_template = """\
package com.andrewsenin.pierogi.ast;

public interface AstVisitor<T> {{
\t{visitors}
}}"""
    with open(output_path.joinpath("AstVisitor.java"), "w") as visitor_file:
        visitor_file.write(visitor_class_template.format(
            visitors=generate_visitors(class_names)
        ))


def read_description_file(path: pathlib.Path) -> str:
    with open(path, "r") as description_file:
        return description_file.read()


def normalize_class_name(class_name: str) -> str:
    return class_name + "Expression"


def parse_class_field_description(line_split: List[str]) -> Dict[str, str]:
    return {field_name: field_type for field_name, field_type in zip(line_split[3::2], line_split[2::2])}


def parse_class_descriptions(descriptions: str) -> (Dict[str, Dict[str, str]], Dict[str, str], Dict[str, str]):
    lines = descriptions.split("\n")
    lines = list(filter(lambda line: line != "", lines))
    unique_classes = {}
    base_classes = {}
    derived_classes = {}
    for line in lines:
        line_split = line.split()
        class_type = line_split[0]
        class_name = line_split[1]
        if class_type == "unique":
            unique_classes[normalize_class_name(class_name)] = parse_class_field_description(line_split)
        elif class_type == "base":
            base_classes[class_name] = parse_class_field_description(line_split)
        else:
            base_class_name = line_split[2]
            derived_classes[normalize_class_name(class_name)] = base_class_name
    return unique_classes, base_classes, derived_classes


def write_class_file(class_name: str, output_path: pathlib.Path, source: str) -> None:
    with open(output_path.joinpath(class_name + ".java"), "w") as java_file:
        java_file.write(source)


def generate_unique_class_file(name: str, fields: Dict[str, str], output_path: pathlib.Path) -> None:
    java_class_template = """\
package com.andrewsenin.pierogi.ast;

public class {class_name} extends LineNumbered implements Expression {{

{fields}

{constructors}

\t@Override
\tpublic <T> T accept(AstVisitor<T> astVisitor) {{
\t\treturn astVisitor.visit(this);
\t}}

\t@Override
\tpublic boolean equals(Object object) {{
\t\tif (!(object instanceof {class_name})) {{
\t\t\treturn false;
\t\t}}
\t\t{class_name} other = ({class_name}) object;
\t\treturn {field_comparisons};
\t}}

{getters}
}}"""
    write_class_file(name, output_path, java_class_template.format(
        class_name=name,
        fields=generate_class_fields(fields),
        constructors=generate_constructors(name, fields),
        field_comparisons=generate_field_comparisons(fields),
        getters=generate_getters(fields)
    ))


def generate_base_class_file(name: str, fields: Dict[str, str], output_path: pathlib.Path) -> None:
    java_class_template = """\
package com.andrewsenin.pierogi.ast;

public abstract class {class_name} extends LineNumbered {{

{fields}

{constructors}

{getters}
}}"""
    write_class_file(name, output_path, java_class_template.format(
        class_name=name,
        fields=generate_class_fields(fields),
        constructors=generate_constructors(name, fields),
        getters=generate_getters(fields)
    ))


def generate_derived_class_file(name: str, base: str, base_fields: Dict[str, str], output_path: pathlib.Path) -> None:
    java_class_template = """\
package com.andrewsenin.pierogi.ast;

public class {class_name} extends {base_name} implements Expression {{

{constructors}

\t@Override
\tpublic <T> T accept(AstVisitor<T> astVisitor) {{
\t\treturn astVisitor.visit(this);
\t}}

\t@Override
\tpublic boolean equals(Object object) {{
\t\tif (!(object instanceof {class_name})) {{
\t\t\treturn false;
\t\t}}
\t\t{class_name} other = ({class_name}) object;
\t\treturn {field_comparisons};
\t}}
}}"""
    write_class_file(name, output_path, java_class_template.format(
        class_name=name,
        base_name=base,
        constructors=generate_derived_constructors(name, base_fields),
        field_comparisons=generate_field_comparisons(base_fields)
    ))


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("input_path", type=pathlib.Path, help="Path to input expression class description file")
    parser.add_argument("output_path", type=pathlib.Path, help="Path to directory of output .java files")
    args = parser.parse_args()

    class_descriptions = read_description_file(args.input_path)
    unique_classes, base_classes, derived_classes = parse_class_descriptions(class_descriptions)

    for name, fields in unique_classes.items():
        generate_unique_class_file(name, fields, args.output_path)

    for name, fields in base_classes.items():
        generate_base_class_file(name, fields, args.output_path)

    for name, base in derived_classes.items():
        generate_derived_class_file(name, base, base_classes[base], args.output_path)

    generate_visitor_class_file(list(unique_classes.keys()) + list(derived_classes.keys()), args.output_path)
