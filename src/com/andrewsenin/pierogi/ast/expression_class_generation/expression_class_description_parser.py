import pathlib
from typing import Dict


def parse_object_name(line: str) -> str:
    name = line.split(':')[0].strip()
    return name


def parse_object_fields(line: str) -> Dict[str, str]:
    line_split = line.split(':')
    if len(line_split) < 2:
        return {}
    field_list_string = line_split[1]
    field_type_name_pairs = [s.split() for s in field_list_string.split(',')]
    field_types_by_name = {pair[1]: pair[0] for pair in field_type_name_pairs}
    return field_types_by_name


def make_object_field_dict_from_source(source: str) -> Dict[str, Dict[str, str]]:
    lines = filter(lambda line: line.strip() != '', source.split('\n'))
    source_dict = {parse_object_name(line): parse_object_fields(line) for line in lines}
    return source_dict


def make_class_dicts_from_file(path: pathlib.Path) -> Dict[str, Dict[str, str]]:
    with open(path, "r") as vogl_file:
        source = vogl_file.read()
    return make_object_field_dict_from_source(source)
