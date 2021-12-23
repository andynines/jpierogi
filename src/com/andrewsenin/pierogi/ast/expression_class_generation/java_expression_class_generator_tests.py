import java_expression_class_generator as jecg

import unittest


class JavaExpressionClassGeneratorTest(unittest.TestCase):
    def test_parsing(self):
        unique_classes, base_classes, derived_classes = jecg.parse_class_descriptions("""\
unique Definition String symbol Expression definition
unique Nil

base Binary Expression left Expression right
derived Addition Binary
derived Subtraction Binary
""")
        self.assertEqual(
            {"DefinitionExpression": {"symbol": "String", "definition": "Expression"},
             "NilExpression": {}},
            unique_classes)
        self.assertEqual(
            {"BinaryExpression": {"left": "Expression", "right": "Expression"}},
            base_classes)
        self.assertEqual(
            {"AdditionExpression": "BinaryExpression",
             "SubtractionExpression": "BinaryExpression"},
            derived_classes)


if __name__ == "__main__":
    unittest.main()
