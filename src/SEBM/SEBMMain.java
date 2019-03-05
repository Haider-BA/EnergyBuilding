package SEBM;

import SEBM.Configuration.FloorPlan;

public class SEBMMain {

	public static void main(String[] args) 
	{
		FloorPlan tFloorPlan = new FloorPlan();
		
//		tFloorPlan.readFloorPlan();
		tFloorPlan.readFloorPlan("./config/FloorPlan.json");
		tFloorPlan.writeFloorPlan();
		
		SEBMUI simUI = new SEBMUI(tFloorPlan);
//		simUI.setUndecorated(true);
		simUI.setResizable(false);
		simUI.setVisible(true);
	}
}
