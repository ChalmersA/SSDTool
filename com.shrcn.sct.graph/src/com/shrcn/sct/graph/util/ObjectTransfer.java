/**
 * Copyright (c) 2007, 2008 上海思弘瑞电力控制技术有限公司.
 * All rights reserved. This program is an eclipse Rich Client Application
 * based Visual Device Develop System.
 */
package com.shrcn.sct.graph.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * 
 * @author 陈春(mailto:cchun@shrcn.com)
 * @version 1.0, 2009-5-13
 */
/*
 * 修改历史
 * $Log: ObjectTransfer.java,v $
 * Revision 1.3  2011/01/06 08:54:38  cchun
 * Update:清理注释
 *
 * Revision 1.2  2010/04/08 08:33:51  cchun
 * Update:修改TYPE_NAME值
 *
 * Revision 1.1  2010/03/09 08:42:35  cchun
 * Refactor:重构包路径
 *
 * Revision 1.1  2010/03/02 07:49:47  cchun
 * Add:添加重构代码
 *
 * Revision 1.1  2010/02/08 10:41:34  cchun
 * Refactor:完成第一阶段重构
 *
 * Revision 1.2  2010/01/21 08:48:42  gj
 * Update:完成UI插件的国际化字符串资源提取
 *
 * Revision 1.1  2009/05/18 06:05:59  cchun
 * Add:添加拖拽处理类
 *
 */
public class ObjectTransfer extends ByteArrayTransfer {
	
	private static ObjectTransfer instance = new ObjectTransfer();
	private static final String TYPE_NAME = "common-object-transfer-format"; //$NON-NLS-1$
	private static final int TYPEID = registerType(TYPE_NAME);

	/**
	 * Returns the singleton inner-signal transfer instance.
	 */
	public static ObjectTransfer getInstance() {
		return instance;
	}

	/**
	 * Avoid explicit instantiation
	 */
	private ObjectTransfer() {
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	/*
	 * Method declared on Transfer.
	 */
	public void javaToNative(Object object, TransferData transferData) {
		byte[] bytes = toByteArray(object);
		if (bytes != null)
			super.javaToNative(bytes, transferData);
	}
	
	/**
	 * 对象转为字节数组
	 * @param object
	 * @return
	 */
	public byte[] toByteArray(Object object) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream(500);
		ObjectOutputStream os = null;
		byte[] bytes = null;
		try {
			os = new ObjectOutputStream(new BufferedOutputStream(byteStream));
			os.writeObject(object);
			os.flush();
			bytes = byteStream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(null != os)
					os.close();
				byteStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

	/*
	 * Method declared on Transfer.
	 */
	public Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}

	/**
	 * 从字节数组读取对象
	 * @param bytes
	 * @return
	 */
	public Object fromByteArray(byte[] bytes) {
		ByteArrayInputStream is = null;
		ObjectInputStream in = null;
		Object object = null;
		try {
			is = new ByteArrayInputStream(bytes);
			in = new ObjectInputStream(is);
			object = in.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if(null != in || null != is)
				try {
					is.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return object;
	}
	
}
