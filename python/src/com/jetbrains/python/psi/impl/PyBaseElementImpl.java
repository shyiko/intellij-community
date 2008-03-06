/*
 * @author max
 */
package com.jetbrains.python.psi.impl;

import com.intellij.extapi.psi.StubBasedPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.IncorrectOperationException;
import com.jetbrains.python.PyTokenTypes;
import com.jetbrains.python.PythonFileType;
import com.jetbrains.python.PythonLanguage;
import com.jetbrains.python.psi.PyElement;
import com.jetbrains.python.psi.PyElementType;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PyBaseElementImpl<T extends StubElement> extends StubBasedPsiElement<T> implements PyElement {
  public PyBaseElementImpl(final PsiElement parent, final T stub, IElementType nodeType) {
    super(parent, stub, nodeType);
  }

  public PyBaseElementImpl(final ASTNode node) {
    super(node);
  }

  @NotNull
    @Override
    public PythonLanguage getLanguage() {
      return (PythonLanguage) PythonFileType.INSTANCE.getLanguage();
  }

  @Override
    public String toString() {
      String className = getClass().getName();
      int pos = className.lastIndexOf('.');
      if (pos >= 0) {
          className = className.substring(pos + 1);
      }
      if (className.endsWith("Impl")) {
          className = className.substring(0, className.length() - 4);
      }
      return className;
  }

  public PsiReference getReference() {
      PsiReference result;
      PsiReference[] refs = getReferences();
      if (refs.length == 0) {
          result = null;
      } else {
          //if (refs.length == 1) {
          result = refs[0];
          //} else {
          //    return new PsiMultiReference(refs, this);
          //}
      }
      return result;
  }

  @NotNull
    public PsiReference[] getReferences() {
      //return getLanguage().getReferenceProviderRegistry().getPythonReferences(
      //        (PyElement) getOriginalElement());
    return PsiReference.EMPTY_ARRAY;
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
      if (visitor instanceof PyElementVisitor) {
          acceptPyVisitor(((PyElementVisitor) visitor));
      } else {
          super.accept(visitor);
      }
  }

  protected void acceptPyVisitor(PyElementVisitor pyVisitor) {
      pyVisitor.visitPyElement(this);
  }

  protected static <T extends PyElement> T[] nodesToPsi(ASTNode[] nodes, T[] array) {
      T[] psiElements = (T[]) java.lang.reflect.Array.newInstance(array.getClass().getComponentType(), nodes.length);
      for (int i = 0; i < nodes.length; i++) {
          //noinspection unchecked
          psiElements[i] = (T) nodes[i].getPsi();
      }
      return psiElements;
  }

  @NotNull
    protected <T extends PyElement> T[] childrenToPsi(TokenSet filterSet, T[] array) {
      final ASTNode[] nodes = getNode().getChildren(filterSet);
      return nodesToPsi(nodes, array);
  }

  @Nullable
    protected <T extends PyElement> T childToPsi(TokenSet filterSet, int index) {
      final ASTNode[] nodes = getNode().getChildren(filterSet);
      if (nodes.length <= index) {
          return null;
      }
      //noinspection unchecked
      return (T) nodes[index].getPsi();
  }

  @Nullable
    protected <T extends PyElement> T childToPsi(IElementType elType) {
      final ASTNode node = getNode().findChildByType(elType);
      if (node == null) {
          return null;
      }
      //noinspection unchecked
      return (T) node.getPsi();
  }

  @NotNull
    protected <T extends PyElement> T childToPsiNotNull(TokenSet filterSet, int index) {
      final PyElement child = childToPsi(filterSet, index);
      if (child == null) {
          throw new RuntimeException("child must not be null");
      }
      //noinspection unchecked
      return (T) child;
  }

  @NotNull
    protected <T extends PyElement> T childToPsiNotNull(IElementType elType) {
      final PyElement child = childToPsi(elType);
      if (child == null) {
          throw new RuntimeException("child must not be null");
      }
      //noinspection unchecked
      return (T) child;
  }

  @Nullable
    public <T extends PyElement> T getContainingElement(Class<T> aClass) {
      PsiElement parent = getParent();
      while (parent != null) {
          if (aClass.isInstance(parent)) {
              //noinspection unchecked
              return (T) parent;
          }
          parent = parent.getParent();
      }
      return null;
  }

  @Nullable
    public PyElement getContainingElement(TokenSet tokenSet) {
      PsiElement parent = getParent();
      while (parent != null) {
          ASTNode node = parent.getNode();
          if (node != null && tokenSet.contains(node.getElementType())) {
              //noinspection UNCHECKED_WARNING
              return (PyElement) parent;
          }
          parent = parent.getParent();
      }
      return null;
  }

  public void delete() throws IncorrectOperationException {
      PsiElement parent = getParent();
      if (parent instanceof PyBaseElementImpl) {
          PyBaseElementImpl pyElement = (PyBaseElementImpl) parent;
          pyElement.deletePyChild(this);
      } else {
          super.delete();
      }
  }

  public PsiElement replace(@NotNull PsiElement element) throws IncorrectOperationException {
      PsiElement parent = getParent();
      if (parent instanceof PyBaseElementImpl) {
          PyBaseElementImpl pyElement = (PyBaseElementImpl) parent;
          return pyElement.replacePyChild(this, element);
      } else {
          return super.replace(element);
      }
  }

  protected void deletePyChild(PyBaseElementImpl element)
          throws IncorrectOperationException {
      throw new IncorrectOperationException("Delete not implemented in "
          + this);
  }

  protected PsiElement replacePyChild(PyElement oldel, PsiElement newel)
          throws IncorrectOperationException {
      if (!oldel.getParent().equals(this)) {
          throw new IncorrectOperationException("Element " + oldel + " is " +
                  "not my child");
      }

      Class<? extends PsiElement> cls = getValidChildClass();
      if (cls == null) {
          throw new IncorrectOperationException("Delete not imlpemented for "
                  + this);
      }
      if (!cls.isInstance(oldel) || !cls.isInstance(newel)) {
          throw new IncorrectOperationException("Elements must be instance "
                  + "of " + cls.getSimpleName() + ", but are " + oldel + ", "
                  + newel);
      }
      PsiElement copy = newel.copy();
      getNode().replaceChild(oldel.getNode(), copy.getNode());
      return copy;
  }

  protected @Nullable Class<? extends PsiElement> getValidChildClass() {
      return null;
  }

  protected static ASTNode getPrevComma(ASTNode after) {
      ASTNode node = after;
      PyElementType comma = PyTokenTypes.COMMA;
      do {
          node = node.getTreePrev();
      }
      while (node != null && !node.getElementType().equals(comma));
      return node;
  }

  protected static ASTNode getNextComma(ASTNode after) {
      ASTNode node = after;
      PyElementType comma = PyTokenTypes.COMMA;
      do {
          node = node.getTreeNext();
      }
      while (node != null && !node.getElementType().equals(comma));
      return node;
  }
}