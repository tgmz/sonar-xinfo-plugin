/*******************************************************************************
  * Copyright (c) 13.11.2016 Thomas Zierer.
  * All rights reserved. This program and the accompanying materials
  * are made available under the terms of the Eclipse Public License v1.0
  * which accompanies this distribution,  and is available at
  * http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *    Thomas Zierer - initial API and implementation and/or initial documentation
  *******************************************************************************/
package de.tgmz.sonar.plugins.xinfo.color.assembler;

/**
 * Assembler S/390 instructions.
 */
public enum Instructions370 {
	A("Add")
	, AH("Add Halfword")
	, AL("Add Logical")
	, ALR("Add Logical Registers")
	, AP("Add Packed (Decimal)")
	, AR("Add Registers")
	, BAL("Branch and Link")
	, BALR("Branch and Link Register")
	, BAS("Branch and Save")
	, BASR("Branch and Save Register")
	, BASSM("Branch and Save(Set Mode)")
	, BC("Branch on Condition")
	, BCR("Branch on Condition Register")
	, BCT("Branch on Count")
	, BCTR("Branch on Count Register")
	, BSM("Branch and Set Mode")
	, BXH("Branch on Index High")
	, BXLE("Branch on Index Low or Equal")
	, C("Compare")
	, CDS("Compare Double and Swap")
	, CH("Compare Halfword")
	, CL("Compare Logical")
	, CLC("Compare Logical Characters")
	, CLCL("Compare Logical Characters Long")
	, CLI("Compare Logical Immediate")
	, CLM("Compare Logical under Mask")
	, CLR("Compare Logical Registers")
	, CP("Compare Packed (Decimal)")
	, CR("Compare Registers")
	, CS("Compare and Swap")
	, CVB("Convert to Binary")
	, CVD("Convert to Decimal")
	, D("Divide")
	, DP("Divide Packed (Decimal)")
	, DR("Divide Registers")
	, ED("Edit")
	, EDMK("Edit and Mark")
	, EX("Execute")
	, IC("Insert Characters")
	, ICM("Insert Character under Mask")
	, L("Load")
	, LA("Load Address")
	, LCR("Load Complement Registers")
	, LH("Load Halfword")
	, LM("Load Multiples")
	, LNR("Load Negative Registers")
	, LPR("Load Positive Registers")
	, LR("Load Register")
	, LTR("Load and Test Register")
	, M("Multiply")
	, MH("Multiply Halfword")
	, MP("Multiply Packed (Decimal)")
	, MR("Multiply Registers")
	, MVC("Move Characters")
	, MVCIN("Move Characters Inverse")
	, MVCL("Move Characters Long")
	, MVI("Move Immediate")
	, MVN("Move Numerics")
	, MVO("Move with Offset")
	, MVZ("Move Zones")
	, N("And")
	, NC("And Characters")
	, NI("And Immediate")
	, NR("And Registers")
	, O("Or")
	, OC("Or Characters")
	, OI("Or Immediate")
	, OR("Or Registers")
	, PACK("Pack")
	, S("Subtract")
	, SH("Subtract Halfword")
	, SL("Subtract Logical")
	, SLA("Shift Left Single")
	, SLDA("Shift Left Double")
	, SLDL("Shift Left Double Logical")
	, SLL("Shift Left Single Logical")
	, SLR("Subtract Logical Registers")
	, SP("Subtract Packed (Decimal)")
	, SR("Subtract Registers")
	, SRA("Shift Right Single")
	, SRDA("Shift Right Double")
	, SRDL("Shift Right Double Logical")
	, SRL("Shift Right Single Logical")
	, SRP("Shift and Round Decimal")
	, ST("Store")
	, STC("Store Character")
	, STCM("Store Characters under Mask")
	, STH("Store Halfword")
	, STM("Store Multiples")
	, SVC("Supervisor Call")
	, TM("Test under Mask")
	, TR("Translate")
	, TRT("Translate and Test")
	, UNPK("Unpack")
	, X("Exclusive Or")
	, XC("Exclusive Or Characters")
	, XI("Exclusive Or Immediate")
	, XR("Exclusive Or Registers")
	, ZAP("Zero and Add Packed");
	
	private String desc;
	
	private Instructions370(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
