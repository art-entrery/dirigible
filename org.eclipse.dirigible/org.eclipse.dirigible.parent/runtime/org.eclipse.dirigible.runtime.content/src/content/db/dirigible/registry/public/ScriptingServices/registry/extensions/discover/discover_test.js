/*eslint-env node */

exports.getItem = function() {
	var item = {
		image: "cogs",
		color: 'red',
		path: "#/scripting/tests",
		title: "Tests",
		description: "Test Cases"
	};
	return item;
};

exports.getOrder = function() {
	return 7;
};
