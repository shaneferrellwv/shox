package edu.ufl.cise.plcsp23;

import java.util.List;
import java.util.ArrayList;

import edu.ufl.cise.plcsp23.IToken.Kind;
import edu.ufl.cise.plcsp23.ast.*;

public class CodeGenerator extends ASTVisitor {
    // private class variable declarations
    StringBuilder packageCode = new StringBuilder();
    int scope;
    List<String> symbolTable = new ArrayList<>();
    boolean stringProgram = false;
    List<String> strings = new ArrayList<>();

    // constructor
    CodeGenerator() {

    }

    // utility functions

    // visitor functions
    @Override
    public Object visitProgram(Program program, Object arg) throws PLCException {
        StringBuilder programCode = new StringBuilder();
        programCode.append("public class ");
        programCode.append(program.getIdent().getName());
        programCode.append(" {\n\tpublic static ");
        if (program.getType() == Type.STRING) {
            programCode.append("String");
            stringProgram = true;
        } else if (program.getType() == Type.IMAGE) {
            if (packageCode.indexOf("BufferedImage") == -1) {
                packageCode.append("import java.awt.image.BufferedImage;\n");
            }
            programCode.append("BufferedImage");
        } else if (program.getType() == Type.PIXEL) {
            programCode.append("int");
        } else {
            programCode.append(program.getType().toString().toLowerCase());
        }
        programCode.append(" apply(");
        scope = 0;
        List<NameDef> params = program.getParamList();
        for (int i = 0; i < params.size(); i++) {
            programCode.append((String)params.get(i).visit(this, arg));
            if (i < params.size() - 1)
                programCode.append(", ");
        }
        programCode.append(") {\n");
        programCode.append(program.getBlock().visit(this, program));
        programCode.insert(0, packageCode + "\n\n");
        programCode.append("\t}\n}");
        return programCode.toString();
    }

    @Override
    public Object visitNameDef(NameDef nameDef, Object arg) throws PLCException {
        StringBuilder nameDefCode = new StringBuilder();
        String name = nameDef.getIdent().getName() + "_" + scope;
        if (nameDef.getType() == Type.STRING) {
            nameDefCode.append("String");
            strings.add(name);
        } else if (nameDef.getType() == Type.IMAGE) {
            if (packageCode.indexOf("BufferedImage") == -1) {
                packageCode.append("import java.awt.image.BufferedImage;\n");
            }
            nameDefCode.append("BufferedImage");
        } else if (nameDef.getType() == Type.PIXEL) {
            nameDefCode.append("int");
        } else {
            nameDefCode.append(nameDef.getType().toString().toLowerCase());
        }
        nameDefCode.append(" ");
        nameDefCode.append(name);
        symbolTable.add(name);
        return nameDefCode.toString();
    }

    @Override
    public Object visitBlock(Block block, Object arg) throws PLCException {
        StringBuilder blockCode = new StringBuilder();
        List<Declaration> decList = block.getDecList();
        for (AST declaration : decList) {
            blockCode.append("\t\t");
            blockCode.append((String)declaration.visit(this, arg));
            blockCode.append(";\n");
        }
        List<Statement> statementList = block.getStatementList();
        for (AST statement : statementList) {
            blockCode.append("\t\t");
            blockCode.append((String)statement.visit(this, arg));
        }
        return blockCode.toString();
    }

    @Override
    public Object visitDeclaration(Declaration declaration, Object arg) throws PLCException {
        StringBuilder declarationCode = new StringBuilder();
        String identName = (String)declaration.getNameDef().visit(this, arg);
        declarationCode.append(identName);
        if (declaration.getNameDef().getType() == Type.IMAGE) {
            if (packageCode.indexOf("BufferedImage") == -1) {
                packageCode.append("import java.awt.image.BufferedImage;\n");
            }
            if (declaration.getNameDef().getDimension() == null) {
                if (declaration.getInitializer().getType() == Type.STRING) {
                    if (packageCode.indexOf("FileURLIO") == -1) {
                        packageCode.append("import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n");
                    }
                    declarationCode.append(" = FileURLIO.readImage(");
                    declarationCode.append((String)declaration.getInitializer().visit(this, arg));
                    declarationCode.append(")");
                    return declarationCode.toString();
                }
                if (declaration.getInitializer().getType() == Type.IMAGE) {
                    if (packageCode.indexOf("ImageOps") == -1) {
                        packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                    }
                    declarationCode.append(" = ImageOps.cloneImage(");
                    declarationCode.append((String)declaration.getInitializer().visit(this, arg));
                    declarationCode.append(")");
                    return declarationCode.toString();

                }
            } else { // nameDef has dimension
                if (declaration.getInitializer() == null) {
                    if (packageCode.indexOf("ImageOps") == -1) {
                        packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                    }
                    declarationCode.append(" = ImageOps.makeImage(");
                    declarationCode
                            .append((String)declaration.getNameDef().getDimension().getWidth().visit(this, arg));
                    declarationCode.append(", ");
                    declarationCode
                            .append((String)declaration.getNameDef().getDimension().getHeight().visit(this, arg));
                    declarationCode.append(")");
                    return declarationCode.toString();
                } else { // declaration has intializer
                    if (declaration.getInitializer().getType() == Type.STRING) {
                        if (packageCode.indexOf("FileURLIO") == -1) {
                            packageCode.append("import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n");
                        }
                        declarationCode.append(" = FileURLIO.readImage(");
                        declarationCode.append((String)declaration.getInitializer().visit(this, arg));
                        declarationCode.append(", ");
                        declarationCode
                                .append((String)declaration.getNameDef().getDimension().getWidth().visit(this, arg));
                        declarationCode.append(", ");
                        declarationCode
                                .append((String)declaration.getNameDef().getDimension().getHeight().visit(this, arg));
                        declarationCode.append(")");
                        return declarationCode.toString();
                    } else if (declaration.getInitializer().getType() == Type.IMAGE) {
                        if (packageCode.indexOf("ImageOps") == -1) {
                            packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                        }
                        declarationCode.append(" = ImageOps.copyAndResize(");
                        declarationCode.append((String)declaration.getInitializer().visit(this, arg));
                        declarationCode.append(", ");
                        declarationCode
                                .append((String)declaration.getNameDef().getDimension().getWidth().visit(this, arg));
                        declarationCode.append(", ");
                        declarationCode
                                .append((String)declaration.getNameDef().getDimension().getHeight().visit(this, arg));
                        declarationCode.append(")");
                        return declarationCode.toString();
                    } else if (declaration.getInitializer().getType() == Type.PIXEL) {
                        if (packageCode.indexOf("ImageOps") == -1) {
                            packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                        }
                        declarationCode.append(" = ImageOps.setAllPixels(ImageOps.makeImage(");
                        declarationCode.append((String)declaration.getNameDef().getDimension().getWidth().visit(this, arg));
                        declarationCode.append(", ");
                        declarationCode.append((String)declaration.getNameDef().getDimension().getHeight().visit(this, arg));
                        declarationCode.append("), ");
                        declarationCode.append((String)declaration.getInitializer().visit(this, arg));
                        declarationCode.append(")");
                        return declarationCode.toString();
                    }
                }
            }
        } else if (declaration.getNameDef().getType() == Type.PIXEL) { // nameDef type is pixel (int)
            if (declaration.getInitializer() != null) {
                declarationCode.append(" = ");
                declarationCode.append((String)declaration.getInitializer().visit(this, arg)); // visit ExpandedPixel
            }
            return declarationCode.toString();
        } else if (declaration.getInitializer() != null) {
            declarationCode.append(" = ");
            if (strings.indexOf(declaration.getNameDef().getIdent().getName() + "_" + scope) != -1) {
                declarationCode
                        .append("String.valueOf(" + (String)declaration.getInitializer().visit(this, arg) + ")");
                return declarationCode.toString();
            }
            declarationCode.append(((String)declaration.getInitializer().visit(this, arg)));
        }
        return declarationCode.toString();
    }

    // ============ complex expressions (conditional, binary, unary, unary postfix)
    // =============
    @Override
    public Object visitBinaryExpr(BinaryExpr binaryExpr, Object arg) throws PLCException {
        StringBuilder binaryExpressionCode = new StringBuilder();
        binaryExpressionCode.append("(");
        StringBuilder leftExpr = new StringBuilder((String)binaryExpr.getLeft().visit(this, arg));
        if (binaryExpr.getLeft().getType() != Type.IMAGE && binaryExpr.getLeft().getType() != Type.PIXEL) {
            switch (binaryExpr.getOp()) {
                case PLUS -> {
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" + ");
                }
                case MINUS -> {
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" - ");
                }
                case TIMES -> {
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" * ");
                }
                case DIV -> {
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" / ");
                }
                case MOD -> {
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" % ");
                }
                case LT -> {
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" < ");
                    binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                    binaryExpressionCode.append(") ? 1 : 0)");
                    return binaryExpressionCode.toString();
                }
                case GT -> {
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" > ");
                    binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                    binaryExpressionCode.append(") ? 1 : 0)");
                    return binaryExpressionCode.toString();
                }
                case LE -> {
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" <= ");
                    binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                    binaryExpressionCode.append(") ? 1 : 0)");
                    return binaryExpressionCode.toString();
                }
                case GE -> {
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" >= ");
                    binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                    binaryExpressionCode.append(") ? 1 : 0)");
                    return binaryExpressionCode.toString();
                }
                case EQ -> {
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" == ");
                    binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                    binaryExpressionCode.append(") ? 1 : 0)");
                    return binaryExpressionCode.toString();
                }
                case OR -> {
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" > 0)");
                    binaryExpressionCode.append(" || ");
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                    binaryExpressionCode.append(" > 0) ? 1 : 0)");
                    return binaryExpressionCode.toString();
                }
                case AND -> {
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" > 0)");
                    binaryExpressionCode.append(" && ");
                    binaryExpressionCode.append("(");
                    binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                    binaryExpressionCode.append(" > 0) ? 1 : 0)");
                    return binaryExpressionCode.toString();
                }
                case EXP -> {
                    if (packageCode.indexOf("Math") == -1) {
                        packageCode.append("import java.lang.Math;\n");
                    }
                    binaryExpressionCode.append("(int)Math.pow(");
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(", ");
                    binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                    binaryExpressionCode.append(")");
                    binaryExpressionCode.append(")");
                    return binaryExpressionCode.toString();
                }
                case BITAND -> {
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" & ");
                }
                case BITOR -> {
                    binaryExpressionCode.append(leftExpr);
                    binaryExpressionCode.append(" | ");
                }
                default -> throw new IllegalArgumentException("Unexpected value: " + binaryExpr.getOp());
            }
            binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
        } else { // working with images and pixels
            if (binaryExpr.getLeft().getType() == Type.IMAGE && binaryExpr.getRight().getType() == Type.IMAGE) {
                if (packageCode.indexOf("ImageOps") == -1) {
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                }
                if (binaryExpr.getOp() == Kind.EQ) {
                    binaryExpressionCode.append("ImageOps.equalsForCodeGen(");
                } else {
                    binaryExpressionCode.append("ImageOps.binaryImageImageOp(ImageOps.OP.");
                    binaryExpressionCode.append(binaryExpr.getOp());
                    binaryExpressionCode.append(", ");
                }
                binaryExpressionCode.append((String)binaryExpr.getLeft().visit(this, arg));
                binaryExpressionCode.append(", ");
                binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                binaryExpressionCode.append(")");
            }
            else if (binaryExpr.getLeft().getType() == Type.IMAGE && binaryExpr.getRight().getType() == Type.INT) {
                if (packageCode.indexOf("ImageOps") == -1) {
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                }
                binaryExpressionCode.append("ImageOps.binaryImageScalarOp(ImageOps.OP.");
                binaryExpressionCode.append(binaryExpr.getOp());
                binaryExpressionCode.append(", ");
                binaryExpressionCode.append((String)binaryExpr.getLeft().visit(this, arg));
                binaryExpressionCode.append(", ");
                binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                binaryExpressionCode.append(")");
            } else if (binaryExpr.getLeft().getType() == Type.PIXEL && binaryExpr.getRight().getType() == Type.PIXEL) {
                if (packageCode.indexOf("PixelOps") == -1) {
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
                }
                if (packageCode.indexOf("ImageOps") == -1) {
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                }
                binaryExpressionCode.append("ImageOps.binaryPackedPixelPixelOp(ImageOps.OP.");
                binaryExpressionCode.append(binaryExpr.getOp());
                binaryExpressionCode.append(", ");
                binaryExpressionCode.append((String)binaryExpr.getLeft().visit(this, arg));
                binaryExpressionCode.append(", ");
                binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                binaryExpressionCode.append(")");
            }
            else if (binaryExpr.getLeft().getType() == Type.PIXEL && binaryExpr.getRight().getType() == Type.INT) {
                if (packageCode.indexOf("PixelOps") == -1) {
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
                }
                if (packageCode.indexOf("ImageOps") == -1) {
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                }
                binaryExpressionCode.append("ImageOps.binaryPackedPixelIntOp(ImageOps.OP.");
                binaryExpressionCode.append(binaryExpr.getOp());
                binaryExpressionCode.append(", ");
                binaryExpressionCode.append((String)binaryExpr.getLeft().visit(this, arg));
                binaryExpressionCode.append(", ");
                binaryExpressionCode.append((String)binaryExpr.getRight().visit(this, arg));
                binaryExpressionCode.append(")");
            }
        }
        binaryExpressionCode.append(")");
        return binaryExpressionCode.toString();
    }

    @Override
    public Object visitConditionalExpr(ConditionalExpr conditionalExpr, Object arg) throws PLCException {
        StringBuilder conditionalExpressionCode = new StringBuilder();
        conditionalExpressionCode.append("(");
        conditionalExpressionCode.append((String)conditionalExpr.getGuard().visit(this, arg));
        conditionalExpressionCode.append(" > 0) ? ");
        conditionalExpressionCode.append((String)conditionalExpr.getTrueCase().visit(this, arg));
        conditionalExpressionCode.append(" : ");
        conditionalExpressionCode.append((String)conditionalExpr.getFalseCase().visit(this, arg));
        return conditionalExpressionCode.toString();
    }

    // ======== primary expressions (string literal, numeric literal, identifier,
    // zero, rand) =========
    @Override
    public Object visitStringLitExpr(StringLitExpr stringLitExpr, Object arg) throws PLCException {
        return "\"" + stringLitExpr.getValue() + "\"";
    }

    @Override
    public Object visitNumLitExpr(NumLitExpr numLitExpr, Object arg) throws PLCException {
        return String.valueOf(numLitExpr.getValue());
    }

    @Override
    public Object visitIdentExpr(IdentExpr identExpr, Object arg) throws PLCException {
        for (int n = scope; n >= 0; n--) {
            if (symbolTable.indexOf(identExpr.getName() + "_" + n) != -1)
                return symbolTable.get(symbolTable.indexOf(identExpr.getName() + "_" + n));
        }
        throw new PLCException("variable " + identExpr.getName() + " undeclared");
    }

    @Override
    public Object visitZExpr(ZExpr zExpr, Object arg) throws PLCException {
        return "255";
    }

    @Override
    public Object visitRandomExpr(RandomExpr randomExpr, Object arg) throws PLCException {
        if (packageCode.indexOf("Math") == -1) {
            packageCode.append("import java.lang.Math;\n");
        }
        return "(Math.floor(Math.random() * 256))";
    }
    // ==============================================================================

    // =============== statements (assignment, write, while, return)
    // ================
    @Override
    public Object visitAssignmentStatement(AssignmentStatement assignmentStatement, Object arg) throws PLCException {
        StringBuilder assignmentStatementCode = new StringBuilder();
        if (assignmentStatement.getLv().getIdent().getType() != Type.IMAGE &&
                assignmentStatement.getLv().getPixelSelector() != null &&
                assignmentStatement.getLv().getColor() != null &&
                assignmentStatement.getE().getType() != Type.IMAGE &&
                assignmentStatement.getE().getType() != Type.STRING) {
                    assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
                    assignmentStatementCode.append(" = ");
                }
        if (assignmentStatement.getLv().getIdent().getType() == Type.PIXEL) { // Variable type is pixel
            if (packageCode.indexOf("PixelOps") == -1) {
                packageCode.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
            }
            assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(" = ");
            assignmentStatementCode.append((String)assignmentStatement.getE().visit(this, arg));
            // Variable type is image, no pixel selector, no color channel
        } else if (assignmentStatement.getLv().getIdent().getType() == Type.IMAGE &&
                assignmentStatement.getLv().getPixelSelector() == null &&
                assignmentStatement.getLv().getColor() == null) {
            if (packageCode.indexOf("PixelOps") == -1) {
                packageCode.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
            }
            if (assignmentStatement.getE().getType() == Type.STRING) { // Right side is string
                if (packageCode.indexOf("FileURLIO") == -1) {
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.FileURLIO;\n");
                }
                assignmentStatementCode.append("ImageOps.copyInto(");
                assignmentStatementCode.append("FileURLIO.readImage(");
                assignmentStatementCode.append((String)assignmentStatement.getE().visit(this, arg));
                assignmentStatementCode.append(")");
                assignmentStatementCode.append(", ");
                assignmentStatementCode.append(assignmentStatement.getLv().getIdent().getName() + "_" + scope);
                assignmentStatementCode.append(")");
            } else if (assignmentStatement.getE().getType() == Type.IMAGE) { // Right side is image
                assignmentStatementCode.append("ImageOps.copyInto(");
                assignmentStatementCode.append((String)assignmentStatement.getE().visit(this, arg));
                assignmentStatementCode.append(", ");
                assignmentStatementCode.append(assignmentStatement.getLv().getIdent().getName() + "_" + scope);
                assignmentStatementCode.append(")");
            }
            else if (assignmentStatement.getE().getType() == Type.PIXEL) { // Right side is pixel
                assignmentStatementCode.append("ImageOps.setAllPixels(");
                assignmentStatementCode.append(assignmentStatement.getLv().getIdent().getName() + "_" + scope);
                assignmentStatementCode.append(", ");
                assignmentStatementCode.append((String)assignmentStatement.getE().visit(this, arg));
                assignmentStatementCode.append(")");
            }
        }
        // Variable type is image with pixel selector, no color channel
        else if (assignmentStatement.getLv().getIdent().getType() == Type.IMAGE &&
            assignmentStatement.getLv().getPixelSelector() != null &&
            assignmentStatement.getLv().getColor() == null) {
            assignmentStatementCode.append("for (int y = 0; y != ");
            assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(".getHeight(); y++) {\n\t\t\tfor (int x = 0; x != ");
            assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(".getWidth(); x++) {\n\t\t\t\tImageOps.setRGB(");
            assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(", ");
            assignmentStatementCode.append((String)assignmentStatement.getLv().getPixelSelector().getX().visit(this,arg));
            assignmentStatementCode.append(", ");
            assignmentStatementCode.append((String)assignmentStatement.getLv().getPixelSelector().getY().visit(this,arg));
            assignmentStatementCode.append(", ");
            assignmentStatementCode.append(assignmentStatement.getE().visit(this, arg));
            assignmentStatementCode.append(");\n\t\t\t}\n\t\t}");
        }
        // Variable type is image with pixel selector and color channel
        else if (assignmentStatement.getLv().getIdent().getType() == Type.IMAGE &&
            assignmentStatement.getLv().getPixelSelector() != null &&
            assignmentStatement.getLv().getColor() != null) {
            assignmentStatementCode.append("for (int y = 0; y != ");
            assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(".getHeight(); y++) {\n\t\t\tfor (int x = 0; x != ");
            assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(".getWidth(); x++) {\n\t\t\t\tImageOps.setRGB(");
            assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(", ");
            assignmentStatementCode.append((String)assignmentStatement.getLv().getPixelSelector().getX().visit(this,arg));
            assignmentStatementCode.append(", ");
            assignmentStatementCode.append((String)assignmentStatement.getLv().getPixelSelector().getY().visit(this,arg));
            assignmentStatementCode.append(", PixelOps.set");
            switch (assignmentStatement.getLv().getColor()) {
                case red -> assignmentStatementCode.append("Red");
                case blu -> assignmentStatementCode.append("Blu");
                case grn -> assignmentStatementCode.append("Grn");
            }
            assignmentStatementCode.append("(ImageOps.getRGB(");
            assignmentStatementCode.append((String)assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(", x, y), ");
            assignmentStatementCode.append(assignmentStatement.getE().visit(this, arg));
            assignmentStatementCode.append("));\n\t\t\t}\n\t\t}");
        } else { // assignment 5
            assignmentStatementCode.append((String) assignmentStatement.getLv().visit(this, arg));
            assignmentStatementCode.append(" = ");
            assignmentStatementCode.append((String) assignmentStatement.getE().visit(this, arg));
        }
        assignmentStatementCode.append(";\n");
        return assignmentStatementCode.toString();
    }

    @Override
    public Object visitWriteStatement(WriteStatement statementWrite, Object arg) throws PLCException {
        StringBuilder writeStatementCode = new StringBuilder();
        if (packageCode.indexOf("Console") == -1) {
            packageCode.append("import edu.ufl.cise.plcsp23.runtime.ConsoleIO;\n");
        } 
        if (statementWrite.getE().getType() != Type.PIXEL)
            writeStatementCode.append("ConsoleIO.write(");
        else 
        writeStatementCode.append("ConsoleIO.writePixel(");
        writeStatementCode.append((String)statementWrite.getE().visit(this, arg));
        writeStatementCode.append(")");
        writeStatementCode.append(";\n");
        return writeStatementCode.toString();
    }

    @Override
    public Object visitWhileStatement(WhileStatement whileStatement, Object arg) throws PLCException {
        StringBuilder whileStatementCode = new StringBuilder();
        whileStatementCode.append("while ((");
        whileStatementCode.append((String)whileStatement.getGuard().visit(this, arg));
        whileStatementCode.append(") != 0)\n\t\t{\n");
        scope++;
        whileStatementCode.append((String)whileStatement.getBlock().visit(this, arg));
        for (int i = 0; i < symbolTable.size(); i++) {
            if (symbolTable.get(i).indexOf("_" + scope, symbolTable.get(i).length() - 2) != -1) {
                symbolTable.remove(i);
            }
        }
        scope--;
        whileStatementCode.append("\t\t}\n");
        return whileStatementCode.toString();
    }

    @Override
    public Object visitReturnStatement(ReturnStatement returnStatement, Object arg) throws PLCException {
        StringBuilder returnStatementCode = new StringBuilder();
        if (stringProgram) {
            if (returnStatement.getE().getType() == Type.IMAGE) {
                returnStatementCode.append("return BufferedImage.toString(");
                returnStatementCode.append(returnStatement.getE().visit(this, arg));
                returnStatementCode.append(");\n");
                return returnStatementCode.toString();
            } else if (returnStatement.getE().getType() == Type.PIXEL) {
                returnStatementCode.append("return Integer.toHexString(");
                returnStatementCode.append(returnStatement.getE().visit(this, arg));
                returnStatementCode.append(");\n");
                return returnStatementCode.toString();
            } 
            returnStatementCode.append("return String.valueOf(");
            returnStatementCode.append(returnStatement.getE().visit(this, arg));
            returnStatementCode.append(");\n");
            return returnStatementCode.toString();
        }
        returnStatementCode.append("return ");
        returnStatementCode.append(returnStatement.getE().visit(this, arg));
        returnStatementCode.append(";\n");
        return returnStatementCode.toString();
    }
    // ===============================================================================

    @Override
    public Object visitLValue(LValue lValue, Object arg) throws PLCException {
        for (int n = scope; n >= 0; n--) {
            if (symbolTable.indexOf(lValue.getIdent().getName() + "_" + n) != -1) {
                return symbolTable.get(symbolTable.indexOf(lValue.getIdent().getName() + "_" + n));
            }
        }
        throw new PLCException("variable " + lValue.getIdent().getName() + " undeclared");
    }

    // ========================= assignment 6 ==================================
    @Override
    public Object visitDimension(Dimension dimension, Object arg) throws PLCException {
        throw new PLCException("not yet implemented");
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr unaryExpr, Object arg) throws PLCException {
        StringBuilder unaryExpressionCode = new StringBuilder();
        switch (unaryExpr.getOp()) {
            case BANG -> {
                unaryExpressionCode.append("(");
                unaryExpressionCode.append(unaryExpr.getE().visit(this, arg));
                unaryExpressionCode.append(" == 0 ? 1 : 0 )");
            }
            case MINUS -> {
                unaryExpressionCode.append("(-");
                unaryExpressionCode.append(unaryExpr.getE().visit(this, arg));
                unaryExpressionCode.append(")");
            }
        }

        return unaryExpressionCode.toString();
    }

    @Override
    public Object visitUnaryExprPostFix(UnaryExprPostfix unaryExprPostfix, Object arg) throws PLCException {
        StringBuilder unaryPostFixCode = new StringBuilder();
        if (unaryExprPostfix.getPrimary().getType() == Type.IMAGE) {
            if (unaryExprPostfix.getPixel() != null && unaryExprPostfix.getColor() == null) { // PrimaryExpr
                                                                                              // PixelSelector ε
                if (packageCode.indexOf("ImageOps") == -1) {
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                }
                unaryPostFixCode.append("ImageOps.getRGB(");
                unaryPostFixCode.append((String)unaryExprPostfix.getPrimary().visit(this, arg));
                unaryPostFixCode.append(", ");
                unaryPostFixCode.append(unaryExprPostfix.getPixel().getX().visit(this, arg));
                unaryPostFixCode.append(", ");
                unaryPostFixCode.append(unaryExprPostfix.getPixel().getY().visit(this, arg));
                unaryPostFixCode.append(")");
            } else if (unaryExprPostfix.getPixel() != null && unaryExprPostfix.getColor() != null) { // PrimaryExpr
                                                                                                     // PixelSelector ε
                if (packageCode.indexOf("ImageOps") == -1)
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                if (packageCode.indexOf("PixelOps") == -1)
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");

                switch (unaryExprPostfix.getColor()) {
                    case red -> unaryPostFixCode.append("PixelOps.red(ImageOps.getRGB(");
                    case grn -> unaryPostFixCode.append("PixelOps.grn(ImageOps.getRGB(");
                    case blu -> unaryPostFixCode.append("PixelOps.blu(ImageOps.getRGB(");
                }
                unaryPostFixCode.append((String)unaryExprPostfix.getPrimary().visit(this, arg));
                unaryPostFixCode.append(", ");
                unaryPostFixCode.append(unaryExprPostfix.getPixel().getX().visit(this, arg));
                unaryPostFixCode.append(", ");
                unaryPostFixCode.append(unaryExprPostfix.getPixel().getY().visit(this, arg));
                unaryPostFixCode.append("))");
            } else if (unaryExprPostfix.getPixel() == null && unaryExprPostfix.getColor() != null) { // PrimaryExpr
                                                                                                     // PixelSelector ε
                if (packageCode.indexOf("ImageOps") == -1)
                    packageCode.append("import edu.ufl.cise.plcsp23.runtime.ImageOps;\n");
                switch (unaryExprPostfix.getColor()) {
                    case red -> unaryPostFixCode.append("ImageOps.extractRed(");
                    case grn -> unaryPostFixCode.append("ImageOps.extractGrn(");
                    case blu -> unaryPostFixCode.append("ImageOps.extractBlu(");
                }
                unaryPostFixCode.append((String)unaryExprPostfix.getPrimary().visit(this, arg));
                unaryPostFixCode.append(")");
            }
        } else if (unaryExprPostfix.getPrimary().getType() == Type.PIXEL) {
            if (packageCode.indexOf("PixelOps") == -1)
                packageCode.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
            switch (unaryExprPostfix.getColor()) {
                case red -> unaryPostFixCode.append("PixelOps.red(");
                case grn -> unaryPostFixCode.append("PixelOps.grn(");
                case blu -> unaryPostFixCode.append("PixelOps.blu(");
            }
            unaryPostFixCode.append((String)unaryExprPostfix.getPrimary().visit(this, arg));
            unaryPostFixCode.append(")");
        }
        return unaryPostFixCode.toString();
    }

    @Override
    public Object visitPixelSelector(PixelSelector pixelSelector, Object arg) throws PLCException {
        throw new PLCException("this code is unreachable, error in parsing");
    }

    @Override
    public Object visitPixelFuncExpr(PixelFuncExpr pixelFuncExpr, Object arg) throws PLCException {
        throw new PLCException("not yet implemented");
    }

    @Override
    public Object visitExpandedPixelExpr(ExpandedPixelExpr expandedPixelExpr, Object arg) throws PLCException {
        StringBuilder expandedPixelCode = new StringBuilder();
        if (packageCode.indexOf("PixelOps") == -1) {
            packageCode.append("import edu.ufl.cise.plcsp23.runtime.PixelOps;\n");
        }
        expandedPixelCode.append("PixelOps.pack(");
        expandedPixelCode.append((String)expandedPixelExpr.getRedExpr().visit(this, arg));
        expandedPixelCode.append(", ");
        expandedPixelCode.append((String)expandedPixelExpr.getGrnExpr().visit(this, arg));
        expandedPixelCode.append(", ");
        expandedPixelCode.append((String)expandedPixelExpr.getBluExpr().visit(this, arg));
        expandedPixelCode.append(")");
        return expandedPixelCode.toString();
    }

    @Override
    public Object visitPredeclaredVarExpr(PredeclaredVarExpr predeclaredVarExpr, Object arg) throws PLCException {
        if (predeclaredVarExpr.getKind() == Kind.RES_x)
            return "x";
        else if (predeclaredVarExpr.getKind() == Kind.RES_y)
            return "y";
        else throw new PLCException("unexpected predeclared variable expression");
    }
}
