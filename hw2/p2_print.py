import ast
import sys

# the script read the program from stdin
# and print the (sorted) var list to stdout

class NameCollector(ast.NodeVisitor):
    def __init__(self):
        super().__init__()
        self.ident_set = set()

    def visit_Name(self, node : ast.Name):
        self.ident_set.add(node.id)

if __name__ == '__main__':
    src = sys.stdin.read()
    prog = ast.parse(src)
    # print(ast.dump(prog, indent=2))

    v = NameCollector()
    v.visit(prog)
    print(sorted(v.ident_set))
