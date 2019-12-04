package com.arkrud.aws.StaticFactories;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.AvailabilityZone;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesRequest;
import com.amazonaws.services.ec2.model.DescribeAvailabilityZonesResult;
import com.amazonaws.services.ec2.model.DescribeSubnetsRequest;
import com.amazonaws.services.ec2.model.DescribeSubnetsResult;
import com.amazonaws.services.ec2.model.DescribeVpcsRequest;
import com.amazonaws.services.ec2.model.DescribeVpcsResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Subnet;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.Vpc;
import com.arkrud.TreeInterface.CustomTreeContainer;
import com.arkrud.aws.AWSAccount;
import com.arkrud.aws.AwsCommon;
import com.arkrud.aws.CustomObjects.CustomAWSObject;
import com.arkrud.aws.CustomObjects.CustomEC2Instance;

public class EC2Common {
	// Connect to EC2
	public static AmazonEC2 connectToEC2(AWSCredentials credentials) {
		@SuppressWarnings("deprecation")
		AmazonEC2 ec2 = new AmazonEC2Client(credentials);
		return ec2;
	}

	// Retrieve AWS account tags
	public static List<com.amazonaws.services.ec2.model.TagDescription> getAccountTags(AWSAccount account) {
		com.amazonaws.services.ec2.model.DescribeTagsResult tagsResut = connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias())).describeTags();
		List<com.amazonaws.services.ec2.model.TagDescription> tags = tagsResut.getTags();
		return tags;
	}

	/// VPC related function
	public static ArrayList<ArrayList<Object>> retriveAvailabilityZones(AWSAccount account) {
		ArrayList<ArrayList<Object>> availabilityZonesData = new ArrayList<ArrayList<Object>>();
		ArrayList<AvailabilityZone> availabilityZones = new ArrayList<AvailabilityZone>();
		Filter f = new Filter().withName("region-name").withValues(account.getAccontRegionObject().getName());
		DescribeAvailabilityZonesRequest request = new DescribeAvailabilityZonesRequest().withFilters(f);
		DescribeAvailabilityZonesResult result = null;
		try {
			AmazonEC2 client = connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			result = client.describeAvailabilityZones(request);
			availabilityZones = new ArrayList<AvailabilityZone>(result.getAvailabilityZones());
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
		}
		Iterator<AvailabilityZone> it = availabilityZones.iterator();
		while (it.hasNext()) {
			AvailabilityZone az = it.next();
			ArrayList<Object> availabilityZoneData = new ArrayList<Object>();
			availabilityZoneData.add(az.getZoneName());
			availabilityZoneData.add(az.getRegionName());
			availabilityZoneData.add(false);
			availabilityZonesData.add(availabilityZoneData);
		}
		return availabilityZonesData;
	}

	public static ArrayList<ArrayList<Object>> getAzToSingleVPCMapping(Vector<String> vpcIDs, ArrayList<ArrayList<Object>> vpcSubnetsData, String vpc) {
		ArrayList<Hashtable<String, ArrayList<Object>>> azVPCMappings = new ArrayList<Hashtable<String, ArrayList<Object>>>();
		Iterator<String> vpcIDsIterator = vpcIDs.iterator();
		while (vpcIDsIterator.hasNext()) {
			String vpcID = vpcIDsIterator.next();
			if (vpcID.equals(vpc + "(Default)")) {
				Hashtable<String, ArrayList<Object>> azVPCMapping = new Hashtable<String, ArrayList<Object>>();
				Iterator<ArrayList<Object>> subnetIterator = vpcSubnetsData.iterator();
				while (subnetIterator.hasNext()) {
					ArrayList<Object> subnetData = subnetIterator.next();
					ArrayList<Object> azData = new ArrayList<Object>();
					if (((String) subnetData.get(3)).equals(vpcID.substring(0, 12))) {
						azData.add(subnetData.get(0));
						azData.add(vpcID);
						azData.add(false);
						azVPCMapping.put((String) subnetData.get(0), azData);
					}
				}
				azVPCMappings.add(azVPCMapping);
			}
		}
		ArrayList<ArrayList<Object>> azsData = new ArrayList<ArrayList<Object>>();
		Iterator<Hashtable<String, ArrayList<Object>>> azVPCMappingsIterator = azVPCMappings.iterator();
		while (azVPCMappingsIterator.hasNext()) {
			Hashtable<String, ArrayList<Object>> map = azVPCMappingsIterator.next();
			Set<String> keys = map.keySet();
			Iterator<String> itr = keys.iterator();
			String str = "";
			while (itr.hasNext()) {
				str = itr.next();
				azsData.add(map.get(str));
			}
		}
		return azsData;
	}

	public static ArrayList<ArrayList<Object>> getAzToVPCMapping(Vector<String> vpcIDs, ArrayList<ArrayList<Object>> vpcSubnetsData) {
		ArrayList<Hashtable<String, ArrayList<Object>>> azVPCMappings = new ArrayList<Hashtable<String, ArrayList<Object>>>();
		Iterator<String> vpcIDsIterator = vpcIDs.iterator();
		while (vpcIDsIterator.hasNext()) {
			String vpcID = vpcIDsIterator.next();
			Hashtable<String, ArrayList<Object>> azVPCMapping = new Hashtable<String, ArrayList<Object>>();
			Iterator<ArrayList<Object>> subnetIterator = vpcSubnetsData.iterator();
			while (subnetIterator.hasNext()) {
				ArrayList<Object> subnetData = subnetIterator.next();
				ArrayList<Object> azData = new ArrayList<Object>();
				if (((String) subnetData.get(3)).equals(vpcID.substring(0, 12))) {
					azData.add(subnetData.get(0));
					azData.add(vpcID);
					azData.add(false);
					azVPCMapping.put((String) subnetData.get(0), azData);
				}
			}
			azVPCMappings.add(azVPCMapping);
		}
		ArrayList<ArrayList<Object>> azsData = new ArrayList<ArrayList<Object>>();
		Iterator<Hashtable<String, ArrayList<Object>>> azVPCMappingsIterator = azVPCMappings.iterator();
		while (azVPCMappingsIterator.hasNext()) {
			Hashtable<String, ArrayList<Object>> map = azVPCMappingsIterator.next();
			Set<String> keys = map.keySet();
			Iterator<String> itr = keys.iterator();
			String str = "";
			while (itr.hasNext()) {
				str = itr.next();
				azsData.add(map.get(str));
			}
		}
		return azsData;
	}

	// Retrieve VPC Subnets groups for AWS account
	public static Subnet retriveVPCSubnets(AWSAccount account, String subnetID) {
		Subnet subnet = null;
		DescribeSubnetsResult describeSubnetsResult = null;
		try {
			AmazonEC2 client = connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
			DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest();
			describeSubnetsResult = client.describeSubnets(describeSubnetsRequest.withSubnetIds(subnetID));
			if (describeSubnetsResult.getSubnets().size() > 0) {
				subnet = describeSubnetsResult.getSubnets().get(0);
			}
		} catch (AmazonServiceException e) {
			e.printStackTrace();
		} catch (AmazonClientException e) {
			e.printStackTrace();
		}
		return subnet;
	}

	// Retrieve VPC Subnet data
	public static ArrayList<ArrayList<Object>> getAccountSubnetsData(AWSAccount account) {
		ArrayList<ArrayList<Object>> vpcSubnetsData = new ArrayList<ArrayList<Object>>();
		DescribeSubnetsRequest describeSubnetsRequest = new DescribeSubnetsRequest();
		AmazonEC2 client = EC2Common.connectToEC2(AwsCommon.getAWSCredentials(account.getAccountAlias()));
		DescribeSubnetsResult res = client.describeSubnets(describeSubnetsRequest);
		ArrayList<Object> epcSubnetData = null;
		Iterator<Subnet> si = res.getSubnets().iterator();
		while (si.hasNext()) {
			Subnet subnet = si.next();
			epcSubnetData = new ArrayList<Object>();
			epcSubnetData.add(subnet.getAvailabilityZone());
			epcSubnetData.add(subnet.getSubnetId());
			epcSubnetData.add(subnet.getCidrBlock());
			epcSubnetData.add(subnet.getVpcId());
			epcSubnetData.add(getEC2ObjectNameTag(subnet));
			epcSubnetData.add(false);
			vpcSubnetsData.add(epcSubnetData);
		}
		return vpcSubnetsData;
	}

	public static ArrayList<ArrayList<Object>> getVPCSubnetsData(ArrayList<ArrayList<Object>> accountSubnetsData, String vpcID) {
		ArrayList<ArrayList<Object>> vpcSubnetsData = new ArrayList<ArrayList<Object>>();
		Iterator<ArrayList<Object>> accountSubnetsDataIterator = accountSubnetsData.iterator();
		while (accountSubnetsDataIterator.hasNext()) {
			ArrayList<Object> accountSubnetData = accountSubnetsDataIterator.next();
			if (((String) accountSubnetData.get(3)).equals(vpcID)) {
				vpcSubnetsData.add(accountSubnetData);
			}
		}
		return vpcSubnetsData;
	}

	public static Vector<String> getVPCIDs(AmazonEC2 ec2) {
		Vector<String> vpcIDs = new Vector<String>();
		DescribeVpcsResult describeVpcsResult = ec2.describeVpcs();
		Iterator<Vpc> vpcit = describeVpcsResult.getVpcs().iterator();
		while (vpcit.hasNext()) {
			String vpcID = vpcit.next().getVpcId();
			if (isVPCDefault(ec2, vpcID)) {
				vpcIDs.add(vpcID + "(Default)");
			} else {
				vpcIDs.add(vpcID);
			}
		}
		return vpcIDs;
	}

	public static boolean isVPCDefault(AmazonEC2 ec2, String vpcID) {
		boolean isDefault = false;
		DescribeVpcsRequest describeVpcsRequest = new DescribeVpcsRequest().withVpcIds(vpcID);
		DescribeVpcsResult describeVpcsResult = ec2.describeVpcs(describeVpcsRequest);
		Iterator<Vpc> vpcit = describeVpcsResult.getVpcs().iterator();
		while (vpcit.hasNext()) {
			isDefault = vpcit.next().isDefault();
		}
		return isDefault;
	}

	public static boolean hasDefaultVPC(AmazonEC2 ec2) {
		boolean hasDefault = false;
		DescribeVpcsRequest describeVpcsRequest = new DescribeVpcsRequest();
		DescribeVpcsResult describeVpcsResult = ec2.describeVpcs(describeVpcsRequest);
		Iterator<Vpc> vpcit = describeVpcsResult.getVpcs().iterator();
		while (vpcit.hasNext()) {
			if (vpcit.next().isDefault()) {
				hasDefault = true;
				break;
			}
		}
		return hasDefault;
	}

	// Retrieve name tag
	public static String getEC2ObjectNameTag(Object ec2Object) {
		String volumeName = "";
		Iterator<Tag> tags = null;
		tags = ((Subnet) ec2Object).getTags().iterator();
		while (tags.hasNext()) {
			Tag tag = tags.next();
			if (tag.getKey().startsWith("Name")) {
				volumeName = tag.getValue();
			}
		}
		return volumeName;
	}

	// Retrieve AWS objects data
	public static ArrayList<ArrayList<Object>> getAWSObjectData(CustomTreeContainer container) {
		ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < container.getEc2Objects().size(); i++) {
			CustomAWSObject awsObject = (CustomAWSObject) container.getEc2Objects().get(i);
			ArrayList<Object> awsObjectData = null;
			if (awsObject instanceof CustomEC2Instance) {
				awsObjectData = awsObject.getAWSObjectSummaryData();
			} else {
				awsObjectData = awsObject.getAWSObjectSummaryData();
			}
			
			data.add(awsObjectData);
		}
		return data;
	}
}
