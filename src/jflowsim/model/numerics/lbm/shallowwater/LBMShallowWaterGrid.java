package jflowsim.model.numerics.lbm.shallowwater;

import java.text.DecimalFormat;
import jflowsim.model.numerics.lbm.LBMUniformGrid;
import jflowsim.model.numerics.lbm.LbEQ;
import jflowsim.model.numerics.utilities.GridNodeType;
import jflowsim.model.numerics.utilities.Scalar;
import jflowsim.view.headupdisplay.HeadUpDisplay;

public class LBMShallowWaterGrid extends LBMUniformGrid {

    public LBMShallowWaterGrid(double _length, double _width, double _dx) {
        super(_length, _width, _dx);
    }

    public LBMShallowWaterGrid(double _length, double _width, int _nx, int _ny) {
        super(_length, _width, _nx, _ny);
    }

    protected void allocateMemory() {
        f = new double[nx * ny * 9];
        ftemp = new double[nx * ny * 9];
        type = new int[nx * ny];

        for (int i = 0; i < nx * ny; i++) {
            f[i] = 0.0;
            ftemp[i] = 0.0;
            type[i] = GridNodeType.FLUID;
        }

        System.out.println("LBMShallowWaterGrid::allocateMemoery() nx:" + nx + " ny:" + ny + " - " + nx * ny);
    }

        public void init(int x, int y, double _h, double _vx, double _vy) {

        double[] feq = new double[9];

        LbEQ.getBGKEquilibriumShallowWater( _h, _vx / dv, _vy / dv, feq, dv, gravity);

        for (int dir = 0; dir < 9; dir++) {
            f[(x + y * nx) * 9 + dir] = feq[dir];
            ftemp[(x + y * nx) * 9 + dir] = feq[dir];
        }
    }


    public void refineGrid(double scaleFactor) {

        System.out.println("Refinement factor: " + scaleFactor);

        int nxOld = this.nx;
        int nyOld = this.ny;

        // update information on the domain size
        this.nx = (int) ((this.nx - 1) * scaleFactor) + 1;
        this.ny = (int) ((this.ny - 1) * scaleFactor) + 1;

        this.dx = this.getLength() / (this.nx - 1);

        this.updateParameters();

        // allocate memory for distribution functions and geo matrix
        double fNew[] = new double[nx * ny * 9];
        double ftempNew[] = new double[nx * ny * 9];
        int typeNew[] = new int[nx * ny];

        System.out.println("LBMShallowWaterGrid::allocateMemoery() nx:" + nx + " ny:" + ny + " - " + nx * ny);

        // constant interpolation of the PDFs and the geo matrix
        for (int x = 0; x < this.nx; x++) {
            for (int y = 0; y < this.ny; y++) {
                // index of source node in old data array
                int xOld = (int) Math.floor(x / scaleFactor);
                int yOld = (int) Math.floor(y / scaleFactor);

                int nodeIndexOld = (yOld * nxOld + xOld) * 9;
                int nodeIndexNew = (y * this.nx + x) * 9;

                for (int dir = 0; dir <= LbEQ.ENDDIR; dir++) {
                    fNew[nodeIndexNew + dir] = f[nodeIndexOld + dir];
                    ftempNew[nodeIndexNew + dir] = ftemp[nodeIndexOld + dir];
                }
                typeNew[nodeIndexNew / 9] = type[nodeIndexOld / 9];
            }
        }

        f = fNew;
        ftemp = ftempNew;
        type = typeNew;
    }

    public double getScalar(int x, int y, int type) {

        if (type == Scalar.V_X) {
            return getVeloX(x, y);
        } else if (type == Scalar.V_Y) {
            return getVeloY(x, y);
        } else if (type == Scalar.V) {
            return Math.sqrt(Math.pow(getVeloX(x, y), 2.0) + Math.pow(getVeloY(x, y), 2.0));
        } else if (type == Scalar.RHO) {
            return getDensity(x, y);
        } else if (type == Scalar.GRID_TYPE) {
            return this.getType(x, y);
        } else if (type == Scalar.T) {
            return 0.0;
        } else {
            System.out.println("unknown scalar value " + type);
            System.exit(-1);
            return -1;
        }
    }

    public void updateHeadUpDisplay(HeadUpDisplay hud) {

        DecimalFormat dfExpo = new DecimalFormat("0.00E0");

        DecimalFormat df = new DecimalFormat("0.00");

        hud.drawText("LBM viscosity: " + dfExpo.format(this.nue_lbm));
        hud.drawText("LBM forcing: " + dfExpo.format(this.forcingX1) + "  ,  " + dfExpo.format(this.forcingX2));
        hud.drawText("v_in_lbm: " + v_in_lbm);
        hud.drawText("V scale: " + df.format(this.dv));
    }
}
