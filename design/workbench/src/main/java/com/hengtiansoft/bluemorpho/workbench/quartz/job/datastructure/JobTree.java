package com.hengtiansoft.bluemorpho.workbench.quartz.job.datastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * 元素不可重复，且可能存在闭环的特殊树
 * 
 * @author <a href="mailto:chendonghuang@hengtiansoft.com"> chendonghuang</a>
 * @version 创建时间：Jun 6, 2018 2:06:33 PM
 */
public class JobTree<E> {

	public static class Node<T> {
		T data;
		// 保存其父节点的位置
		List<Integer> parents = new ArrayList<Integer>();

		public Node() {
		}

		public Node(T data) {
			this.data = data;
		}

		public Node(T data, int parent) {
			this.data = data;
			this.parents.add(new Integer(parent));
		}

		public Node(T data, List<Integer> poses) {
			this.data = data;
			for (Integer pos : poses) {
				this.parents.add(pos);
			}
		}

		public void addParent(int parent) {
			if (!this.parents.contains(parent)) {
				this.parents.add(parent);
			}
		}
		
		public String toString() {
			return "Tree$Node[data=" + data + ", parent=" + parents.toString() + "]";
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public List<Integer> getParents() {
			return parents;
		}

		public void setParents(List<Integer> parents) {
			this.parents = parents;
		}
	}

	private final int DEFAULT_TREE_SIZE = 100;
	private int treeSize = 0;
	// 使用一个Node[]数组来记录该树里的所有节点
	private Node<E>[] nodes;
	// 记录树的节点数
	private int nodeNums;
	// 当前分析job树针对的project
	private String projectId;
	// 当前分析job树针对的project的代码版本
	private String codeVersion;
	// 当前job tree对应的前台请求，后台接收时间
	private String requestReceivedTime;
	private boolean allDone = false;
	
	// 以指定节点创建树
	public JobTree(E data) {
		treeSize = DEFAULT_TREE_SIZE;
		nodes = new Node[treeSize];
		nodes[0] = new Node<E>(data, -1);
		nodeNums++;
	}

	// 以指定根节点、指定treeSize创建树
	public JobTree(E data, int treeSize, String projectId, String codeVersion, String time) {
		this.treeSize = treeSize;
		nodes = new Node[treeSize];
		nodes[0] = new Node<E>(data, -1);
		nodeNums++;
		this.projectId = projectId;
		this.codeVersion = codeVersion;
		this.requestReceivedTime = time;
	}

	// 为指定节点添加子节点(当该子结点已被其他父节点添加过，则只新增其一个父节点位置信息)
	public Node<E> addNode(E data, Node parent) {
		for (int i = 0; i < treeSize; i++) {
			// 结点以添加过，只补全其parent位置信息
			if (nodes[i] == data) {
				nodes[i].addParent(pos(parent));
				return nodes[i];
			}
			// 找到数组中第一个为null的元素，该元素保存新节点
			if (nodes[i] == null) {
				// 创建新节点，并用指定的数组元素保存它
				nodes[i] = new Node(data, pos(parent));
				nodeNums++;
				return nodes[i];
			}
		}
		throw new RuntimeException("该树已满，无法添加新节点");
	}
	
	// 为指定节点添加子节点
	public Node<E> addNode(E data, List<Node> parents) {
		for (int i = 0; i < treeSize; i++) {
			// 找到数组中第一个为null的元素，该元素保存新节点
			if (nodes[i] == null) {
				List<Integer> poses = new ArrayList<Integer>();
				// 创建新节点，并用指定的数组元素保存它
				for (Node parent : parents) {
					int pos = pos(parent);
					poses.add(new Integer(pos));
				}
				nodes[i] = new Node(data, poses);
				nodeNums++;
				return nodes[i];
			}
		}
		throw new RuntimeException("该树已满，无法添加新节点");
	}

	// 判断树是否为空
	public boolean empty() {
		// 根结点是否为null
		return nodes[0] == null;
	}

	// 返回根节点
	public Node<E> root() {
		// 返回根节点
		return nodes[0];
	}

	// 返回指定节点（非根结点）的父节点
	public List<Node<E>> parents(Node node) {
		// 每个节点的parents记录了其父节点的位置
		List<Node<E>> parentNodes = new ArrayList<Node<E>>();
		List<Integer> poses = node.parents;
		for (Integer pos : poses) {
			parentNodes.add(nodes[pos.intValue()]);
		}
		return parentNodes;
	}

	// 返回指定节点（非叶子节点）往下任意层的所有子节点
	public List<Node<E>> anyLevelChildren(Node<E> parent) {
		List<Node<E>> list = new ArrayList<Node<E>>();
		for (int i = 0; i < treeSize; i++) {
			Node<E> currentNode = nodes[i];
			if (currentNode != null) {
				boolean isChild = isChildOfOneNode(parent, currentNode);
				// 其child不包含本身
				if (isChild && !list.contains(currentNode) && pos(parent) != pos(currentNode)) {
					list.add(currentNode);
				}
			}
		}
		return list;
	}

	private boolean isChildOfOneNode(Node<E> oneNode, Node<E> currentNode) {
		if (pos(oneNode) == pos(currentNode)) {
			return true;
		}
		List<Integer> parentsPos = currentNode.parents;
		for (Integer parPos : parentsPos) {
			int parPosIntValue = parPos.intValue();
			if (parPosIntValue != -1) {
				if (parPosIntValue == pos(oneNode)) {
					return true;
				}
				List<Integer> grandFathers = nodes[parPosIntValue].parents;
				for (Integer grandFather : grandFathers) {
					int gf = grandFather.intValue();
					if (gf != -1) {
						return isChildOfOneNode(oneNode, nodes[gf]);
					}
				}
			}
		}
		return false;
	}

	// 返回指定节点（非叶子节点）的所有子节点
	public List<Node<E>> oneLevelChildren(Node<E> parent) {
		List<Node<E>> list = new ArrayList<Node<E>>();
		for (int i = 0; i < treeSize; i++) {
			// 如果当前节点的父节点的位置等于parent节点的位置
			Node<E> currentNode = nodes[i];
			if (currentNode != null) {
				List<Integer> positions = currentNode.parents;
				for (Integer position : positions) {
					if (position.intValue() == pos(parent)) {
						list.add(currentNode);
					}
				}
			}
		}
		return list;
	}

	// 返回该树的深度
//	public int deep() {
//		// 用于记录节点的最大深度
//		int max = 0;
//		for (int i = 0; i < treeSize && nodes[i] != null; i++) {
//			// 初始化本节点的深度
//			int def = 1;
//			// m 记录当前节点的父节点的位置
//			int m = nodes[i].parent;
//			// 如果其父节点存在
//			while (m != -1 && nodes[m] != null) {
//				// 向上继续搜索父节点
//				m = nodes[m].parent;
//				def++;
//			}
//			if (max < def) {
//				max = def;
//			}
//		}
//		return max;
//	}

	// 返回包含指定值的节点
	public int pos(Node node) {
		for (int i = 0; i < treeSize; i++) {
			// 找到指定节点
			if (nodes[i] == node) {
				return i;
			}
		}
		return -1;
	}

	public Node<E>[] getNodes() {
		return nodes;
	}
	
	public List<E> getNodeValues() {
		List<E> values = new ArrayList<E>();
		for (Node<E> node : this.nodes) {
			if (node != null) {
				E data = node.data;
				values.add(data);
			}
		}
		return values;
	}
	
	public Node<E> getNodeByPosition(int pos) {
		return this.nodes[pos];
	}
	
	// 返回所有叶节点value
	public List<E> getLeaves() {
		List<E> result = new ArrayList<E>();
		for (int i = 0; i < this.nodes.length; i++) {
			boolean isLeaf = true;
			for (int j = 0; j < this.nodes.length; j++) {
				if (i != j && this.nodes[j].parents.contains(i)) {
					isLeaf = false;
					break;
				}
			}
			if (isLeaf) {
				result.add(this.nodes[i].getData());
			}
		}
		return result;
	}
	
	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public String getCodeVersion() {
		return codeVersion;
	}

	public void setCodeVersion(String codeVersion) {
		this.codeVersion = codeVersion;
	}

	public String getRequestReceivedTime() {
		return requestReceivedTime;
	}

	public void setRequestReceivedTime(String requestReceivedTime) {
		this.requestReceivedTime = requestReceivedTime;
	}

	public boolean isAllDone() {
		return allDone;
	}

	public void setAllDone(boolean allDone) {
		this.allDone = allDone;
	}
	
}