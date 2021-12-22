import expression_class_description_parser

import unittest


class VoglParserTest(unittest.TestCase):

    def test_object_name_parsing(self):
        self.assertEqual(expression_class_description_parser.parse_object_name("negation: expression operand"), "negation")

    def test_object_fields_parsing(self):
        self.assertEqual(expression_class_description_parser.parse_object_fields("negation: expression operand"), {"operand": "expression"})
        self.assertEqual(expression_class_description_parser.parse_object_fields("binary: expression lhs, token op, expression rhs"),
                         {
                             "lhs": "expression",
                             "op": "token",
                             "rhs": "expression"
                         })
        self.assertEqual(expression_class_description_parser.parse_object_fields("body: list<expression> expressions"),
                         {"expressions": "list<expression>"})
        self.assertEqual(expression_class_description_parser.parse_object_fields("memberless"), {})

    def test_complete_parsing(self):
        self.assertEqual(expression_class_description_parser.make_object_field_dict_from_source("""
            negation: expression operand
            binary: expression lhs, token op, expression rhs
            call: string name, list args
            empty
            """),
                         {
                             "negation": {"operand": "expression"},
                             "binary": {
                                 "lhs": "expression",
                                 "op": "token",
                                 "rhs": "expression"
                             },
                             "call": {
                                 "name": "string",
                                 "args": "list"
                             },
                             "empty": {}
                         })


if __name__ == "__main__":
    unittest.main()
