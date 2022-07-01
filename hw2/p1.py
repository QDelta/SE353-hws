import re

CIDENT = re.compile('[_a-zA-Z][_a-zA-Z0-9]*')
UCAMELCASE = re.compile('([A-Z][a-z]*[0-9]*)+')
SCIFP = re.compile('-?([0-9]+|([0-9]+)?.[0-9]+)e-?[0-9]+')

def pat_test(pat : re.Pattern, s : str):
    if pat.fullmatch(s):
        print(f'"{s}" matches the pattern {pat}')
    else:
        print(f'"{s}" does not match the pattern {pat}')

if __name__ == '__main__':
    pat_test(CIDENT, 'liftA1')
    pat_test(CIDENT, 'concat_map')
    pat_test(CIDENT, '_reserved')
    pat_test(CIDENT, '1num')
    pat_test(CIDENT, 'lisp-like')
    pat_test(CIDENT, 'operator+')

    print()

    pat_test(UCAMELCASE, 'UpperCamelCase')
    pat_test(UCAMELCASE, 'Img2Col123')
    pat_test(UCAMELCASE, 'ALLUPPER')
    pat_test(UCAMELCASE, 'snake_case')
    pat_test(UCAMELCASE, 'lowerCamelCase')
    pat_test(UCAMELCASE, 'AAA_BBB')

    print()

    pat_test(SCIFP, '1e-5')
    pat_test(SCIFP, '-.4e9')
    pat_test(SCIFP, '1.4e-10')
    pat_test(SCIFP, '123')
    pat_test(SCIFP, '1.2.3e1')
    pat_test(SCIFP, '12.-1e1')
