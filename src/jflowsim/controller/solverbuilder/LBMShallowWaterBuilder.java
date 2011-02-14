package jflowsim.controller.solverbuilder;

import jflowsim.model.numerics.Solver;
import jflowsim.model.numerics.UniformGrid;
import jflowsim.model.numerics.lbm.shallowwater.LBMShallowWaterSolver;
import jflowsim.model.numerics.lbm.testcases.CylinderTestCase;
import jflowsim.model.numerics.lbm.testcases.TestCase;

public class LBMShallowWaterBuilder extends SolverBuilder {

    public LBMShallowWaterBuilder() {
        testCaseSet.put(CylinderTestCase.class.getSimpleName(), new CylinderTestCase());
    }

    public Solver createSolver(UniformGrid grid) {
        return new LBMShallowWaterSolver(grid);
    }

    public UniformGrid createGrid(String testcase) {
        TestCase testCaseCreator = testCaseSet.get(testcase);

        return testCaseCreator.getGrid();
    }
}