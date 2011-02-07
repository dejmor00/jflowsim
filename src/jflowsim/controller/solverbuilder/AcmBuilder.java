package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.acm.AcmSolver;
import jflowsim.model.numerics.lbm.testcases.SonjasTestCase;
import jflowsim.model.numerics.lbm.testcases.TaylorGreenVortexTestCase;
import jflowsim.model.numerics.lbm.testcases.TestCaseCreator;

public class AcmBuilder extends SolverBuilder {

    public AcmBuilder() {
        testCaseSet.put(TaylorGreenVortexTestCase.class.getSimpleName(), new TaylorGreenVortexTestCase());
        testCaseSet.put(SonjasTestCase.class.getSimpleName(), new SonjasTestCase());
    }

    public Solver createSolver(UniformGrid grid) {
        return new AcmSolver(grid);
    }

    public UniformGrid createGrid(String testcase) {
        TestCaseCreator testCaseCreator = testCaseSet.get(testcase);

        return testCaseCreator.getGrid();
    }
}