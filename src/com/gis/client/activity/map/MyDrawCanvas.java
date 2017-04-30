package com.gis.client.activity.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.map.TextItem;
import com.baidu.mapapi.map.TextOverlay;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.gis.client.R;
import com.gis.client.activity.query.tree.TreeResultActivity;
import com.gis.client.model.CommonObject;
import com.gis.client.model.Line;
import com.gis.client.model.Switch;
import com.gis.client.model.SwitchInfo;
import com.gis.client.model.Transformer;
import com.gis.client.util.DateUtils;

public class MyDrawCanvas {

	private List<Line> mLineList = new ArrayList<Line>();

	private List<Transformer> mTransformerList = new ArrayList<Transformer>();

	private List<Switch> mSwitchList = new ArrayList<Switch>();

	private HashMap<String, String> mLineStatusMap = new HashMap<String, String>();

	private Map<Integer, SwitchInfo> mSwitchInfoMap;

	private Context mContext;

	public static boolean mIsSeleted;


	private MapView mMapView;
	
	private Line mLine;
	
	private int mStrokeWidth = 2;
	
	public MyDrawCanvas(Context context, List<Line> lineList,
			List<Transformer> voltageList, List<Switch> switchList,
			Map<Integer, SwitchInfo> switchInfoMap,
			HashMap<String, String> lineStatus, MapView mapView) {
		this.mContext = context;

		this.mLineList = lineList;
		this.mSwitchList = switchList;
		this.mTransformerList = voltageList;
		this.mSwitchInfoMap = switchInfoMap;
		this.mLineStatusMap = lineStatus;
		this.mMapView = mapView;
		
		mapView.regMapTouchListner(touchListener);
		init(mapView);
	}
	
	MKMapTouchListener touchListener = new MKMapTouchListener() {
		@Override
		public void onMapLongClick(GeoPoint arg0) {
		}
		@Override
		public void onMapDoubleClick(GeoPoint arg0) {
		}
		@Override
		public void onMapClick(GeoPoint arg0) {
			if (mIsSeleted) {
				mLine = null;
				new ClickTack().execute(arg0, mMapView);
			}
		}
	};
	
	private void init(MapView mapView) {
		
		int level = (int) mapView.getZoomLevel();
		if (level > 12) {
			mStrokeWidth += level - 12;
		}

		GetLineList(mapView);
		drawTransformer(mapView);
		drawSwitch(mapView);

	}
	
	private void GetLineList(MapView mapView) {
		if (mLineList.size() > 0) {
			for (int i = 0; i < mLineList.size(); i++) {
				GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mapView);
				Line line = mLineList.get(i);
				String isPower = line.getIsPower();
				int fId = line.getLineNumber();
				if (mLineStatusMap != null && mLineStatusMap.size() > 0) {
					if (mLineStatusMap.containsKey(String.valueOf(fId))) {
						isPower = mLineStatusMap.get(String.valueOf(fId));
					}
				}
				mMapView.getOverlays().add(graphicsOverlay);
				graphicsOverlay.setData(drawClickLine(line, 0, isPower));
			}
		}
	}
	
	private Graphic drawClickLine(Line line, int type, String isPower) {
		List<Double> latList = line.getLatitudeList();
		List<Double> lonList = line.getLongitudeList();
		//设定折线点坐标
		GeoPoint[] linePoints = new GeoPoint[lonList.size()];
		if (lonList != null && lonList.size() > 0) {
			for (int j = 0; j < lonList.size(); j++) {
				double startLat = latList.get(j);
				double startLon = lonList.get(j);
				GeoPoint startGeoPoint = new GeoPoint((int) startLat,
						(int) startLon);
				startGeoPoint = CoordinateConvert.fromGcjToBaidu(startGeoPoint);
				linePoints[j] = startGeoPoint;
			}
			//构建线
			Geometry lineGeometry = new Geometry();
						
			lineGeometry.setPolyLine(linePoints);
			//设定样式
			Symbol lineSymbol = new Symbol();
			Symbol.Color lineColor = lineSymbol.new Color();
			switch (type) {
			case 0:
				// 有电
				if (isPower != null
						&& isPower.equals(mContext.getResources()
								.getString(R.string.str_power))) {
					lineColor.red = 255;
					lineColor.green = 0;
					lineColor.blue = 0;
					lineColor.alpha = 255;
				} else {
					lineColor.red = 102;
					lineColor.green = 102;
					lineColor.blue = 102;
					lineColor.alpha = 255;
				}
				break;
			case 1:
				lineColor.red = 0;
				lineColor.green = 0;
				lineColor.blue = 0;
				lineColor.alpha = 255;
				break;
			default:
				break;
			}
			lineSymbol.setLineSymbol(lineColor, mStrokeWidth + 2);
			//生成Graphic对象
			Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
			mMapView.refresh();
			return lineGraphic;

		}
		return null;
	}
	/**
	 * 画变压器
	 * 
	 * @param canvas
	 * @param mapView
	 */
	private void drawTransformer(MapView mapView) {
		if (mTransformerList != null && mTransformerList.size() > 0) {
			for (int i = 0; i < mTransformerList.size(); i++) {
				GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
				Transformer transformer = mTransformerList.get(i);
				double lat = transformer.getLatitude();
				double lon = transformer.getLongitude();
				GeoPoint geoPoint = new GeoPoint((int) lat, (int) lon);
				geoPoint = CoordinateConvert.fromGcjToBaidu(geoPoint);
				int level = (int) mapView.getZoomLevel();
				int radius = 500;
				if (level > 12) {
					radius += (level - 12) * 6;
				} else {
					radius -= (12 - level) * 3;
				}
				//构建圆
		  		Geometry circleGeometry = new Geometry();
		  	
		  		//设置圆中心点坐标和半径
		  		circleGeometry.setCircle(geoPoint, radius);
		  		//设置样式
		  		Symbol circleSymbol = new Symbol();
		 		Symbol.Color circleColor = circleSymbol.new Color();
		 		circleColor.red = 0;
		 		circleColor.green = 0;
		 		circleColor.blue = 255;
		 		circleColor.alpha = 255;
		  		circleSymbol.setSurface(circleColor,0,3);
		  		//生成Graphic对象
		  		Graphic circleGraphic = new Graphic(circleGeometry, circleSymbol);
		  		mMapView.getOverlays().add(graphicsOverlay);
		  		graphicsOverlay.setData(circleGraphic);
		  		mMapView.refresh();
			}
		}
	}
	/**
	 * 画开关
	 * 
	 * @param canvas
	 * @param mapView
	 */
	private void drawSwitch(MapView mapView) {
		if (mSwitchList != null && mSwitchList.size() > 0) {
			int level = (int) mapView.getZoomLevel();
			int textSpace = 12;
			int textSize = 12;

			for (int i = 0; i < mSwitchList.size(); i++) {
				GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
				float length = 8;
				if (level > 12) {
					length += (level - 12) * 10;
					textSpace += level - 12;
					textSize += (level - 12);
				} else {
					if (12 - level <= 4) {
						length -= (12 - level) * 5;
					} else {
						length = 1;
					}
				}
				Switch switch1 = mSwitchList.get(i);
				double lat = switch1.getLatitude();
				double lon = switch1.getLongitude();
				int angle = switch1.getAngle();
				int boardId = switch1.getBoard();
				String name = switch1.getName();

				GeoPoint centerGeoPoint = new GeoPoint((int) lat, (int) lon);
				centerGeoPoint = CoordinateConvert.fromGcjToBaidu(centerGeoPoint);
				Point point = mapView.getProjection().toPixels(centerGeoPoint, null);
				float height = length * 1.5f;
				int centerX = point.x;
				int centerY = point.y;
				int oneX = (int) (centerX - length / 2);
				int oneY = (int) (centerY + height / 2);
				int twoX = oneX;
				int twoY = (int) (oneY - height);
				int threeX = (int) (oneX + length);
				int threeY = twoY;
				int fourX = (int) (oneX + length);
				int fourY = oneY;
				
				if (angle != 0 && angle % 90 != 0) {
					angle = 90 - angle;// 以与y轴的夹角为角度,之前的算法是与x轴的夹角为角度
					// 求第一个坐标点
					double tempAngle = Math.toDegrees(Math
							.atan(length / height));
					double tempXb = Math
							.sqrt(height * height + length * length);// 对角线长度
					double tempH = (tempXb / 2)
							* Math.sin(Math.toRadians(tempAngle + angle));
					double tempL = (tempXb / 2)
							* Math.cos(Math.toRadians(tempAngle + angle));
					oneX = (int) (centerX - tempL);
					oneY = (int) (centerY + tempH);
					// 求第二个坐标点
					tempL = length * Math.sin(Math.toRadians(angle));
					tempH = length * Math.cos(Math.toRadians(angle));
					twoX = (int) (oneX - tempL);
					twoY = (int) (oneY - tempH);
					// 求第三个坐标点
					tempL = tempXb
							* Math.cos(Math.toRadians(tempAngle + angle));
					tempH = tempXb
							* Math.sin(Math.toRadians(tempAngle + angle));
					threeX = (int) (oneX + tempL);
					threeY = (int) (oneY - tempH);
					// 求第四个坐标点
					tempL = height * Math.cos(Math.toRadians(angle));
					tempH = height * Math.sin(Math.toRadians(angle));
					fourX = (int) (oneX + tempL);
					fourY = (int) (oneY - tempH);
				} else if (angle / 90.0f == 1 || angle / 90.0f == 3) {// 90°、270°
					oneX = (int) (centerX - height / 2);
					oneY = (int) (centerY + length / 2);
					twoX = oneX;
					twoY = (int) (oneY - length);
					threeX = (int) (oneX + height);
					threeY = twoY;
					fourX = (int) (oneX + height);
					fourY = oneY;
				}
				GeoPoint oneGeoPoint = mapView.getProjection().fromPixels(oneX, oneY);
				GeoPoint twoGeoPoint = mapView.getProjection().fromPixels(twoX, twoY);
				GeoPoint threeGeoPoint = mapView.getProjection().fromPixels(threeX, threeY);
				GeoPoint fourGeoPoint = mapView.getProjection().fromPixels(fourX, fourY);
				//构建多边形
				Geometry polygonGeometry = new Geometry();
				//设置多边形坐标,当储能时用多边形表示实心
				GeoPoint[] polygonPoints = new GeoPoint[4];
				//设置线坐标,当不储能是用线表示空心
				GeoPoint[] lineGeoPoints = new GeoPoint[5];
				polygonPoints[0] = oneGeoPoint;
				polygonPoints[1] = twoGeoPoint;
				polygonPoints[2] = threeGeoPoint; 
				polygonPoints[3] = fourGeoPoint;
				
				lineGeoPoints[0] = oneGeoPoint;
				lineGeoPoints[1] = twoGeoPoint;
				lineGeoPoints[2] = threeGeoPoint; 
				lineGeoPoints[3] = fourGeoPoint;
				lineGeoPoints[4] = oneGeoPoint;
//				polygonGeometry.setPolygon(polygonPoints);
//				polygonGeometry.setPolyLine(polygonPoints);
				//设置多边形样式
				Symbol polygonSymbol = new Symbol();
				Symbol.Color polygonColor = polygonSymbol.new Color();
				if (mSwitchInfoMap != null
						&& mSwitchInfoMap.containsKey(boardId)) {
					SwitchInfo switchInfo = mSwitchInfoMap.get(boardId);
					int infoBoardId = switchInfo.getBoardNumber();
					
					if (infoBoardId == boardId) {
						int status = switchInfo.getSwitchStatus();
						int isSave = switchInfo.getIsSave();
						int voltage = switchInfo.getVoltage();
						// int isFlash = switchInfo.getIsFlash();
						int aL = switchInfo.getaL();
						int bL = switchInfo.getbL();
						int cL = switchInfo.getcL();
						int oL = switchInfo.getoL();

						// 开关状态:0---合；1--分
						if (0 == status) {
							polygonColor.red = 255;
					 		polygonColor.green = 0;
					 		polygonColor.blue = 0;
					 		polygonColor.alpha = 255;
						} else if (1 == status) {
							// 分，绿色
							polygonColor.red = 0;
					 		polygonColor.green = 255;
					 		polygonColor.blue = 0;
					 		polygonColor.alpha = 255;
						} else {
							// 未知,灰色
							polygonColor.red = 102;
					 		polygonColor.green = 102;
					 		polygonColor.blue = 102;
					 		polygonColor.alpha = 255;
							voltage = aL =  cL = oL = 0;
						}
						String time = switchInfo.getTime();
						long lTime = DateUtils.getLongCurrentTime();
						if (time != null && !"".equals(time)) {
							lTime = DateUtils.timeStrToLong(time);
						}
						long lCurTime = DateUtils.getLongCurrentTime();
						if (lCurTime - lTime > 10 * 60 * 1000) {
							// 长时间没更新的,灰色
							polygonColor.red = 102;
					 		polygonColor.green = 102;
					 		polygonColor.blue = 102;
					 		polygonColor.alpha = 255;
							voltage = aL =  cL = oL = 0;
						}
						// 0---储能,实心; 1--不储能,空心
						if (0 == isSave) {
							polygonSymbol.setSurface(polygonColor,1,mStrokeWidth);
							polygonGeometry.setPolygon(polygonPoints);
						} else {
							polygonSymbol.setSurface(polygonColor,0,mStrokeWidth - 1);
							polygonGeometry.setPolyLine(lineGeoPoints);
						}
						
						GeoPoint nGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace);
				  		GeoPoint uGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace *2 );
						GeoPoint aGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace * 3);
						GeoPoint bGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace * 4);
						GeoPoint cGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace * 5);
						GeoPoint oGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace * 6);
						//构建文字
					   	TextItem nItem = new TextItem();
				    	//设置文字位置
					   	nItem.pt = nGeoPoint;
				    	//设置文件内容
					   	nItem.text = name;
					   	//构建文字
					   	TextItem uItem = new TextItem();
					   	//设置文字位置
					   	uItem.pt = uGeoPoint;
					   	//设置文件内容
					   	uItem.text = "U:" + voltage;
					   	//构建文字
					   	TextItem aItem = new TextItem();
					   	//设置文字位置
					   	aItem.pt = aGeoPoint;
					   	//设置文件内容
					   	aItem.text = "Ia:" + aL;
					   	TextItem bItem = new TextItem();
					   	//设置文字位置
					   	bItem.pt = bGeoPoint;
					   	//设置文件内容
					   	bItem.text = "Ib:" + bL;
					   	TextItem cItem = new TextItem();
					   	//设置文字位置
					   	cItem.pt = cGeoPoint;
					   	//设置文件内容
					   	cItem.text = "Ic:" + cL;
					   	TextItem oItem = new TextItem();
					   	//设置文字位置
					   	oItem.pt = oGeoPoint;
					   	//设置文件内容
					   	oItem.text = "Io:" + oL;
					   	nItem.fontColor = polygonColor;
					   	uItem.fontColor = polygonColor;
					   	aItem.fontColor = polygonColor;
					   	bItem.fontColor = polygonColor;
					   	cItem.fontColor = polygonColor;
					   	oItem.fontColor = polygonColor;
					   	nItem.fontSize = textSize; 
					   	uItem.fontSize = textSize; 
					   	aItem.fontSize = textSize; 
					   	bItem.fontSize = textSize; 
					   	cItem.fontSize = textSize; 
					   	oItem.fontSize = textSize; 
					   	TextOverlay textOverlay = new TextOverlay(mMapView);
					   	mapView.getOverlays().add(textOverlay);
					   	textOverlay.addText(nItem);
					   	textOverlay.addText(uItem);
					   	textOverlay.addText(aItem);
					   	textOverlay.addText(bItem);
					   	textOverlay.addText(cItem);
					   	textOverlay.addText(oItem);
					   	mapView.refresh();
					}
					//生成Graphic对象
			  		Graphic polygonGraphic = new Graphic(polygonGeometry, polygonSymbol);
			  		graphicsOverlay.setData(polygonGraphic);
			  		mapView.getOverlays().add(graphicsOverlay);
			  		mapView.refresh();
				} else {
					polygonColor.red = 102;
			 		polygonColor.green = 102;
			 		polygonColor.blue = 102;
			 		polygonColor.alpha = 255;
			 		polygonGeometry.setPolygon(polygonPoints);
			 		polygonSymbol.setSurface(polygonColor,0,mStrokeWidth);
			 		Graphic polygonGraphic = new Graphic(polygonGeometry, polygonSymbol);
			  		graphicsOverlay.setData(polygonGraphic);
			  		mapView.getOverlays().add(graphicsOverlay);
			  		mapView.refresh();
			  		GeoPoint nGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace);
			  		GeoPoint uGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace *2 );
					GeoPoint aGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace * 3);
					GeoPoint bGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace * 4);
					GeoPoint cGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace * 5);
					GeoPoint oGeoPoint = mapView.getProjection().fromPixels(fourX, fourY + textSpace * 6);
					//构建文字
				   	TextItem nItem = new TextItem();
			    	//设置文字位置
				   	nItem.pt = nGeoPoint;
			    	//设置文件内容
				   	nItem.text = name;
					//构建文字
				   	TextItem uItem = new TextItem();
			    	//设置文字位置
				   	uItem.pt = uGeoPoint;
			    	//设置文件内容
				   	uItem.text = "U:" + 0;
				   	//构建文字
				   	TextItem aItem = new TextItem();
				   	//设置文字位置
				   	aItem.pt = aGeoPoint;
				   	//设置文件内容
				   	aItem.text = "Ia:" + 0;
				   	TextItem bItem = new TextItem();
				   	//设置文字位置
				   	bItem.pt = bGeoPoint;
				   	//设置文件内容
				   	bItem.text = "Ib:" + 0;
				   	TextItem cItem = new TextItem();
				   	//设置文字位置
				   	cItem.pt = cGeoPoint;
				   	//设置文件内容
				   	cItem.text = "Ic:" + 0;
				   	TextItem oItem = new TextItem();
				   	//设置文字位置
				   	oItem.pt = oGeoPoint;
				   	//设置文件内容
				   	oItem.text = "Io:" + 0;
				   	nItem.fontColor = polygonColor;
				   	uItem.fontColor = polygonColor;
				   	aItem.fontColor = polygonColor;
				   	bItem.fontColor = polygonColor;
				   	cItem.fontColor = polygonColor;
				   	oItem.fontColor = polygonColor;
					nItem.fontSize = uItem.fontSize = aItem.fontSize = bItem.fontSize = cItem.fontSize = oItem.fontSize = textSize;
				   	TextOverlay textOverlay = new TextOverlay(mMapView);
				   	mapView.getOverlays().add(textOverlay);
				   	textOverlay.addText(nItem);
				   	textOverlay.addText(uItem);
				   	textOverlay.addText(aItem);
				   	textOverlay.addText(bItem);
				   	textOverlay.addText(cItem);
				   	textOverlay.addText(oItem);
				   	mapView.refresh();
				}
			}
		}
	}
	class ClickTack extends AsyncTask<Object, Void, CommonObject> {

		@Override
		protected CommonObject doInBackground(Object... params) {
			GeoPoint cGeoPoint = (GeoPoint) params[0];
			MapView mapView = (MapView) params[1];
			CommonObject object = null;
			object = isClickTransformer(cGeoPoint, mapView);
			if (object == null) {
				object = isClickSwitch(cGeoPoint, mapView);
			}
			if (object == null) {
				object = isClickLine(cGeoPoint, mapView);
			}
			return object;
		}

		@Override
		protected void onPostExecute(CommonObject result) {
			super.onPostExecute(result);
			if (result != null) {
				if (result instanceof Line) {
					init(mMapView);
					mLine = (Line) result;
					GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
					mMapView.getOverlays().add(graphicsOverlay);
					graphicsOverlay.setData(drawClickLine(mLine, 1, null));
				}
				Intent intent = new Intent(mContext, TreeResultActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("result", result);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		}
	}
	/**
	 * 判读是否点击线上
	 * 
	 * @param canvas
	 * @param mapView
	 */
	private Line isClickLine(GeoPoint cGeoPoint, MapView mapView) {
		Point cPoint = mapView.getProjection().toPixels(cGeoPoint, null);
		float cX = cPoint.x;
		float cY = cPoint.y;
		if (mLineList != null && mLineList.size() > 0) {
			for (int i = 0; i < mLineList.size(); i++) {
				Line line = mLineList.get(i);
				List<Double> latList = line.getLatitudeList();
				List<Double> lonList = line.getLongitudeList();
				if (lonList != null && lonList.size() > 0) {
					for (int j = 0; j < lonList.size() - 1; j++) {
						double startLat = latList.get(j);
						double startLon = lonList.get(j);
						double endLat = latList.get(j + 1);
						double endLon = lonList.get(j + 1);
						GeoPoint startGeoPoint = new GeoPoint((int) startLat,
								(int) startLon);
						startGeoPoint = CoordinateConvert.fromGcjToBaidu(startGeoPoint);
						GeoPoint endGeoPoint = new GeoPoint((int) endLat,
								(int) endLon);
						endGeoPoint = CoordinateConvert.fromGcjToBaidu(endGeoPoint);
						Point startPoint = mapView.getProjection().toPixels(
								startGeoPoint, null);
						Point endPoint = mapView.getProjection().toPixels(
								endGeoPoint, null);
						float startX = startPoint.x;
						float startY = startPoint.y;
						float endX = endPoint.x;
						float endY = endPoint.y;
						float k = 0;
						float b = startY;
						double sDistance;
						int level = (int) mapView.getZoomLevel();
						int temp = 10;
						if (level < 10) {
							temp = level;
						}
						// 判断是否点击的算法:1、是否符合直线方程，2、点到直线/点的距离小于一定的值3、点必须在已知的两点之间
						float minX = (startX < endX) ? startX : endX;
						float maxX = (startX > endX) ? startX : endX;
						float minY = (startY < endY) ? startY : endY;
						float maxY = (startY > endY) ? startY : endY;
						if (startX != endX && startY != endY) {
							k = (endY - startY) / (endX - startX);
							b = startY - k * startX;
							sDistance = Math.abs(k * cX - cY + b)
									/ (Math.sqrt(k * k + 1));
							if (cY == k * cX + b || sDistance < temp) {
								if (minX - temp < cX && cX < maxX + temp
										&& minY - temp < cY && cY < maxY + temp) {
									return line;
								}
							}
						} else {
							if (startX == endX && startY == endY) {// 两点重合
								sDistance = Math.sqrt((startX - cX)
										* (startX - cX) + (startY - cY)
										* (startY - cY));
								if (sDistance < temp) {
									return line;
								}
							} else if (startX == endX) {// 平行于Y轴
								sDistance = Math.abs(cX - startX);
								if (cX == startX || sDistance < temp) {
									if (minY - temp < cY && cY < maxY) {
										return line;
									}
								}
							} else {// 平行于X轴
								sDistance = Math.abs(cY - startY);
								if (cY == startY || sDistance < temp) {
									if (minX - temp < cX && cX < maxX + temp) {
										return line;
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}
	/**
	 * @param cGeoPoinit 点击的点
	 * @Desc: 判断是否点击了电压
	 * @author wpt
	 * @since 2013-7-16 下午1:51:12
	 */
	private Transformer isClickTransformer(GeoPoint cGeoPoint, MapView mapView) {
//		Point cPoint = mapView.getProjection().toPixels(cGeoPoint, null);
//		float cX = cPoint.x;
//		float cY = cPoint.y;
		if (mTransformerList != null && mTransformerList.size() > 0) {
			for (int i = 0; i < mTransformerList.size(); i++) {
				Transformer transformer = mTransformerList.get(i);
				double Tlat = transformer.getLatitude();
				double Tlon = transformer.getLongitude();
				GeoPoint tGeoPoint = new GeoPoint((int) Tlat, (int) Tlon);
				tGeoPoint = CoordinateConvert.fromGcjToBaidu(tGeoPoint);
//				Point tPoint = mapView.getProjection()
//						.toPixels(tGeoPoint, null);
//				int level = (int) mapView.getZoomLevel();
				float radius = 500;
//				float tX = tPoint.x;
//				float tY = tPoint.y;
//				float distance = (cX - tX) * (cX - tX) + (cY - tY) * (cY - tY);
//				if (level > 12) {
//					radius += (level - 12) * 6;
//				} else {
//					radius -= (12 - level) * 3;
//				}
//				if (distance < radius * radius || distance == radius * radius) {
//					return transformer;
//				}
				double distance = DistanceUtil.getDistance(cGeoPoint, tGeoPoint);
				if (distance <= radius) {
					return transformer;
				}
			}
		}
		return null;
	}
	/**
	 * @Desc: 判断是否点击了开关
	 * @author wpt
	 * @since 2013-7-16 下午1:51:12
	 */
	private Switch isClickSwitch(GeoPoint cGeoPoint, MapView mapView) {
		Point cPoint = mapView.getProjection().toPixels(cGeoPoint, null);
		float cX = cPoint.x;
		float cY = cPoint.y;
		if (mSwitchList != null && mSwitchList.size() > 0) {
			int level = (int) mapView.getZoomLevel();
			for (int i = 0; i < mSwitchList.size(); i++) {
				float length = 30;
				if (level > 12) {
					length += (level - 12) * 10;
				} else {
					if (12 - level <= 4) {
						length -= (12 - level) * 5;
					} else {
						length = 1;
					}
				}
				Switch switch1 = mSwitchList.get(i);
				double sLat = switch1.getLatitude();
				double sLon = switch1.getLongitude();
				GeoPoint geoPoint = new GeoPoint((int) sLat, (int) sLon);
				geoPoint = CoordinateConvert.fromGcjToBaidu(geoPoint);
				Point point = mapView.getProjection().toPixels(geoPoint, null);

				float height = length * 1.5f;
				float centerX = point.x;
				float centerY = point.y;

				double sDistance = height / 2;
				double cDistance = (cX - centerX) * (cX - centerX)
						+ (cY - centerY) * (cY - centerY);
				if (Math.sqrt(cDistance) <= sDistance
						|| (centerX == cX && centerY == cY)) {
					return switch1;
				}
			}
		}
		return null;
	}
	
}
