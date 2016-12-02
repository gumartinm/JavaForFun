Sandro Mancuso: testing a refactoring legacy code
https://www.youtube.com/watch?v=_NnElPO5BU0

Rules for refactoring legacy code.

1. You may not change production code if not covered by tests. Before refactoring some legacy code we must write a Unit Test for it.

   But some times for writing the test we must make modifications in the legacy code. If such modifications are required: just automated refactoring (via IDES) are allowed, if needed to write the test.


Steps:

1. Start testing the legacy code following the shortest branch.

2. In Unit Test we should not invoke other classes but the one being under test (the other classes should be stub, mocke, etc, etc)

3. When the legacy code is covered by a unit test we may refactor it (never before) We have to refactor our legacy code from the deepest to the shortest branch.

