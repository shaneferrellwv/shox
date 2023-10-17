package edu.ufl.cise.plcsp23.ast;

import edu.ufl.cise.plcsp23.IToken;

public enum ColorChannel {
	red,
	grn,
	blu;

	public static ColorChannel getColor(IToken token) {
		return switch(token.getKind()) {
		case RES_red -> red;
		case RES_grn -> grn;
		case RES_blu -> blu;
		default -> throw new RuntimeException("error in ColorChannel.getColor, unexpected token kind " + token.getKind());
		};
	}
}
