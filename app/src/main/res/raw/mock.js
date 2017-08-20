if (typeof(window.unitedPropertiesIf) == "undefined") {
	window.unitedPropertiesIf = {
		getSessionVariable: function(name) {
			var res = localStorage.getItem(name);
			if (res == null) return "";
			return res;
		},
		getProperty: function(name) {
			if (name == "ordered_songs") return JSON.stringify(['Hopes and Dreams', 'A Neon Glow Lights the Way', 'Welcome To VA-11 HALL-A', 'Every Day is Night', 'Commencing Simulation', 'Drive Me Wild', 'Good for Health, Bad for Education', 'Who Was I?', 'Troubling News', 'A Gaze That Invited Disaster', 'Friendly Conversation', "You've Got Me", 'Umemoto', "JC Elton's", 'Go! Go! Streaming-chan!', 'All Systems, Go!', 'Where Do I Go From Here?', 'Will You Remember Me?', 'Everything Will Be Okay', 'March of the White Knights', 'A. Rene', 'Neo Avatar', 'Those Who Dwell in The Shadows', 'Nighttime Manuvers', 'A Star Pierces the Darkness', 'Your Love is a Drug', 'Through the Storm We Will Find a Way', 'Synthestitch', 'Snowfall', 'The Answer Lies Within', 'Dawn Approaches', 'With Renewed Hope, We Continue Forward', 'Last Call', 'Reminiscence', 'Believe in Me Who Believes in You', 'Final Result', 'Until We Meet Again', 'Digital Drive', 'Safe Haven']);
			if (name == "all_themes") return JSON.stringify(["normal", "dotted", "steam", "kira", "meme"]);
			var res = localStorage.getItem(name);
			if (res == null) return "";
			return res;
		},
		setSessionVariable: function(name, value) {
			localStorage.setItem(name, value);
		},
		setProperty: function(name, value) {
			localStorage.setItem(name, value)
		},
		playSong: function(name, bool) {
			localStorage.setItem("is_playing", true);
			localStorage.setItem("current_song", name);
		},
		playSound: function(name) {
			new Audio("../../assets/" + name).play();
		},
		launchHTML: function(url) {
			var child = window.open(url.replace("file:///android_res/raw/", ""))
			child._parent = window;
		},
		closeWindow: function() {
			if (window.parent != window.top) {
				window.parent.location.reload();
			}
			if (typeof(window._parent) != "undefined") {
				window._parent.location.reload();
			}
			if (typeof(window.opener) != "undefined") {
				window.opener.location.reload();
			}
			window.close();
		},
		toast: function(text) {
			alert(text);
		}
	}
}

