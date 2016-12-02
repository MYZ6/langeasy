/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.chenyi.socket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Arun Gupta
 */
@ClientEndpoint
public class MyClientEndpoint {
	@OnOpen
	public void onOpen(Session session) {
		System.out.println("Connected to endpoint: " + session.getBasicRemote());
		try {
			JSONObject json = new JSONObject();
			json.put("user", "hy1234");
			json.put("pass", "123456");
			json.put("device", 1);
			json.put("ctype", 1);
			String loginReq = "1000 " + json.toString();
			System.out.println("Sending message to endpoint: " + loginReq);
			session.getBasicRemote().sendText(loginReq);
		} catch (IOException ex) {
			Logger.getLogger(MyClientEndpoint.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@OnMessage
	public void processMessage(String message, Session session) throws IOException {
		System.out.println("Received message in client: " + message);

		GameDataProcess(message, session);
		// String gamecode = "1100 {\"msg\":\"gamecode\"}";
		// System.out.println("Sending message to endpoint: " + gamecode);
		// session.getBasicRemote().sendText(gamecode);
		Client.messageLatch.countDown();
	}

	@OnError
	public void processError(Throwable t) {
		t.printStackTrace();
	}

	private void GameDataProcess(String message, Session session) throws IOException {
		String currentPosition = "";
		Integer currentGame = 0;
		Integer currentTable = 0;

		String[] myarray = message.split(" ");
		if (myarray.length < 2) {
			return;
		}
		JSONObject ReData = new JSONObject();
		JSONArray ReDataArr = new JSONArray();
		if (myarray[1].startsWith("[")) {
			ReDataArr = new JSONArray(myarray[1]);
		} else {
			ReData = new JSONObject(myarray[1]);
		}
		switch (myarray[0]) {
		case "1100":
			// sendlogin();
			break;
		case "1001":// 登入桌子
			// sStorage.cid = ReData.cid;
			// sStorage.gid = ReData.gid;
			// sStorage.tid = ReData.tid;
			// // $('.chip-table div').each(function (i, ele) {
			// // $('.prepare-chip .text-cell', this).html(0);
			// // });
			// $('.btn-chip').hide();
			// entertable(ReData.gid, ReData.tid);
			// if (currentPosition != "table") gotoPosition("table");
			break;
		case "2000":// 登陆返回消息
			String alertmsg = "";
			switch (ReData.getString("msg")) {
			case "e1":
				alertmsg = "账号或密码错误";
				break;
			case "e2":
				alertmsg = "账号已停用";
				break;
			case "e3":
				alertmsg = "账号已在线";
				break;
			case "e4":
				alertmsg = "验证码错误";
				break;
			case "e6":
				alertmsg = "没有该桌登录权限";
				break;
			case "e7":
				alertmsg = "桌子不在线";
			case "e8":
				alertmsg = "api登入错误请输入登录!";
				break;
			}
			if (currentPosition == "login") {
				// $("#loginsub").removeAttr("disabled");
			}
			if (alertmsg != "") {
				// alert(alertmsg);
				System.out.println(alertmsg);
				return;
			} else {
				// sStorage.UserName=$("#loginusername").val();
				// sStorage.PassWord=$("#loginpassword").val();
				// sStorage.UserID = ReData.uid;
				// sStorage.VideoUrl=ReData.videourl;
				// sStorage.ClientType = ReData.ctype;
				// if (!heartrunning) {
				// heartrunning = true;
				// var intervalId = window.setInterval("heartbeat()", 30000);
				// }
				// UserXH = ReData.xh;
				// if (UserXH.length > 0 && sStorage.ClientType == 1) {
				// UserDefaultXH = UserXH[0].Id;
				// }
				// sStorage.UserIp = ReData.ip;
				// sStorage.UserAddress = ReData.address;
				// sStorage.GameID = 0;
				// sStorage.ServerTime = ReData.tick;
				// sStorage.killhe = ReData.killhe;
				// sStorage.isapi = ReData.isapi;
				// sStorage.noximaliang = ReData.noximaliang;
				// //sStorage.UserName = ReData.name;
				// var sendmsg = { uid: ReData.uid };
				// ws.send(msgtemplate1.format("1004", JSON.stringify(sendmsg)));
				JSONObject json = new JSONObject();
				json.put("uid", ReData.get("uid"));
				String reqStr = "1004 " + json.toString();
				System.out.println("Sending message to endpoint: " + reqStr);
				session.getBasicRemote().sendText(reqStr);
				// $("#currentuser").html(sStorage.UserName);
				// $("#halluser").html(sStorage.UserName);
			}
			break;
		case "1200":
			// sStorage.UserPassword = ReData.msg;
			// alert("修改密码成功！");
			System.out.println("修改密码成功！");
			break;
		case "2002":
			// sStorage.VideoUrl.videourl =ReData.msg;
			switch (currentPosition) {
			case "table":
				// DeskVideoPlay(ReData.msg);
				break;
			}
			break;
		case "2003":// 重新进入大厅成功
			// if (sStorage.UserID == ReData.uid)
			// {
			// sStorage.TableID = 0;
			// sStorage.TmpBeted = [];
			// sStorage.TmpAllBeted=[];
			// gotoPosition("hall");
			// if(player) player.pause();
			// }
			break;
		case "2004":// 大厅当前桌子基本信息【路子、限红】
			// sStorage.TableID = 0;
			// currentTable = 0;
			// sStorage.TableName = "";
			// Desks = ReData.sort(
			// function (a, b) {
			// if (a.gid < b.gid) return -1;
			// if (a.gid > b.gid) return 1;
			// if (a.tid < b.tid) return -1;
			// if (a.tid > b.tid) return 1;
			// return 0;
			// }
			// );
			if (currentPosition != "hall") {
				// gotoPosition("hall");
			}
			// TimerIntervalId = window.setInterval("TableTimerLeft()", 1000);
			break;
		case "2203":// 有桌子上线
			// Desks.push(ReData);
			// Desks = Desks.sort(
			// function (a, b) {
			// if (a.gid < b.gid) return -1;
			// if (a.gid > b.gid) return 1;
			// if (a.tid < b.tid) return -1;
			// if (a.tid > b.tid) return 1;
			// return 0;
			// }
			// );
			// loadHallTableList(currentGame);
			break;
		case "2204":// 有桌子下线
			// for (var onlinecount = Desks.length - 1; onlinecount >= 0; onlinecount--)
			// {
			// if (Desks[onlinecount].cid ==ReData.cid && Desks[onlinecount].gid == ReData.gid && Desks[onlinecount].tid
			// ==ReData.tid)
			// {
			// Desks.splice(onlinecount,1);
			// }
			// }
			//
			// loadHallTableList(currentGame);
			break;
		case "6001":// 下一场
			// $(Desks).each(function (i, item) {
			// if (item.gid == ReData.gid&&item.tid==ReData.tid) {
			// item.ch=ReData.ch;
			// item.ci=0;
			// item.data = "";
			// }
			// });
			// if (currentPosition == "hall") loadHallTableList(currentGame);
			// if (currentPosition == "table") {
			// if (currentGame == ReData.gid && currentTable == ReData.tid) {
			// entertable(ReData.gid, ReData.tid);
			// $("#chang-ci").html(ReData.tid+ "-" +ReData.ch + "-" + ReData.ci);
			// }
			// }

			break;
		case "6002":
			// $(Desks).each(function (i, item) {
			// if (item.gid == ReData.gid && item.tid == ReData.tid) {
			// item.ch = ReData.ch;
			// item.ci = ReData.ci;
			// item.ttime = ReData.ttime;
			// item.cstate = ReData.cstate;
			// }
			// });
			if (currentPosition == "hall")
				// loadHallTableList(currentGame);
				if (currentPosition == "table") {
					if (currentGame == ReData.getInt("gid") && currentTable == ReData.getInt("tid")) {
						switch (ReData.getString("cstate")) {
						case "init":
							// $('#CurrentTableTime').html(ReData.ttime);
							// soundplay("qxz");
							break;
						case "stop":
							// clearprebet();
							// $('#CurrentTableTime').html('停止下注');
							// soundplay("tzxz");
							break;
						case "close":
							// clearprebet();
							// $('#CurrentTableTime').html('开牌中');
							break;
						}
						// $("#chang-ci").html(ReData.tid + "-" + ReData.ch + "-" + ReData.ci);
					}
				}
			break;
		// 用户金额
		case "6003":
			// $("#currentmoney").html(ReData.msg);
			// $("#hallmoney").html(ReData.msg);
			break;
		// 已下
		case "6004":
			// UserBeted.push(ReData);
			break;
		// 公告
		case "6005":
			// $("#marqueemsg").html(ReData.msg);
			break;
		// 彩池
		case "6007":
			// AllUserBeted.push(ReData);
			break;
		case "6008":
			// for (var count = UserBeted.length - 1; count >= 0; count--) {
			// if (UserBeted[count].cid == ReData.cid && UserBeted[count].gid == ReData.gid && UserBeted[count].tid ==
			// ReData.tid) {
			// UserBeted.splice(count, 1);
			// }
			// }
			// var Beted = {cid:ReData.cid,gid:ReData.gid,tid:ReData.tid,vmoney:ReData.bet};
			// UserBeted.push(Beted);
			// var errorMsg = '';
			String errorMsg = "";
			switch (ReData.getInt("state")) {
			case 1:
				errorMsg = "下注失败!";
				break;

			case 2:
				errorMsg = "余额不足!";
				break;

			case 3:
				errorMsg = "下注失败!";
				break;
			case 4:
				errorMsg = "高于最高限红!";
				break;

			case 5:// 下注成功
				errorMsg = "下注成功!";
				// $("#z-certain-area").html(ReData.bet.Z);
				// $("#x-certain-area").html(ReData.bet.X);
				// $("#h-certain-area").html(ReData.bet.H);
				// $("#zd-certain-area").html(ReData.bet.ZD);
				// $("#xd-certain-area").html(ReData.bet.XD);
				break;
			}
			// clearprebet();
			// alert(errorMsg);
			System.out.println(errorMsg);
			break;
		case "6009":
			// $(Desks).each(function (i, item) {
			// if (item.gid == ReData.gid && item.tid == ReData.tid) {
			// item.data = ReData.data;
			// }
			// });
			// loadHallTableSingle(ReData.gid, ReData.tid);
			// if (ReData.userwin) {
			// UserMoney = ReData.userwin.money;
			// showuserwin(ReData);
			// }
			if (currentGame == ReData.getInt("gid") && currentTable == ReData.getInt("tid")) {
				// $('#CurrentTableTime').html('');
				// playresult(ReData);
				// clearsubebet();
				// refreshRoad(currenttable);
			}

			break;
		case "6105":// 包台
			// $(Desks).each(function (i, item) {
			// if (item.gid == ReData.gid && item.tid == ReData.tid) {
			// item.monopolize = ReData.monopolize;
			// }
			// });
			break;
		}
	};
}