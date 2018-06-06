if (typeof(window.unitedPropertiesIf) == "undefined") {
    var ordered_songs = null, map = {};
    var put = function put(map, whatever, filename, song) {
        map[song] = filename;
    }
    put(map, ordered_songs, "hopes_and_dreams.mp3", "Hopes and Dreams");
    put(map, ordered_songs, "a_neon_glow_lights_the_way.mp3", "A Neon Glow Lights the Way");
    put(map, ordered_songs, "welcome_to_va_11_hall_a.mp3", "Welcome To VA-11 HALL-A");
    put(map, ordered_songs, "every_day_is_night.mp3", "Every Day is Night");
    put(map, ordered_songs, "commencing_simulation.mp3", "Commencing Simulation");
    put(map, ordered_songs, "drive_me_wild.mp3", "Drive Me Wild");
    put(map, ordered_songs, "good_for_health_bad_for_education.mp3", "Good for Health, Bad for Education");
    put(map, ordered_songs, "who_was_i.mp3", "Who Was I?");
    put(map, ordered_songs, "troubling_news.mp3", "Troubling News");
    put(map, ordered_songs, "a_gaze_that_invited_disaster.mp3", "A Gaze That Invited Disaster");
    put(map, ordered_songs, "friendly_conversation.mp3", "Friendly Conversation");
    put(map, ordered_songs, "youve_got_me.mp3", "You've Got Me");
    put(map, ordered_songs, "umemoto.mp3", "Umemoto");
    put(map, ordered_songs, "jc_eltons.mp3", "JC Elton's");
    put(map, ordered_songs, "go_go_streaming_chan.mp3", "Go! Go! Streaming-chan!");
    put(map, ordered_songs, "all_systems_go.mp3", "All Systems, Go!");
    put(map, ordered_songs, "where_do_i_go_from_here.mp3", "Where Do I Go From Here?");
    put(map, ordered_songs, "will_you_remember_me.mp3", "Will You Remember Me?");
    put(map, ordered_songs, "everything_will_be_okay.mp3", "Everything Will Be Okay");
    put(map, ordered_songs, "march_of_the_white_knights.mp3", "March of the White Knights");
    put(map, ordered_songs, "a_rene.mp3", "A. Rene");
    put(map, ordered_songs, "neo_avatar.mp3", "Neo Avatar");
    put(map, ordered_songs, "those_who_dwell_in_the_shadows.mp3", "Those Who Dwell in The Shadows");
    put(map, ordered_songs, "nightime_maneuvers.mp3", "Nighttime Manuvers");
    put(map, ordered_songs, "a_star_pierces_the_darkness.mp3", "A Star Pierces the Darkness");
    put(map, ordered_songs, "your_love_is_a_drug.mp3", "Your Love is a Drug");
    put(map, ordered_songs, "through_the_storm_we_will_find_a_way.mp3", "Through the Storm We Will Find a Way");
    put(map, ordered_songs, "synthestitch.mp3", "Synthestitch");
    put(map, ordered_songs, "snowfall.mp3", "Snowfall");
    put(map, ordered_songs, "the_answer_lies_within.mp3", "The Answer Lies Within");
    put(map, ordered_songs, "dawn_approaches.mp3", "Dawn Approaches");
    put(map, ordered_songs, "with_renewed_hope_we_continue_forward.mp3", "With Renewed Hope, We Continue Forward");
    put(map, ordered_songs, "last_call.mp3", "Last Call");
    put(map, ordered_songs, "reminescence.mp3", "Reminiscence");
    put(map, ordered_songs, "believe_in_me_who_believes_in_you.mp3", "Believe in Me Who Believes in You");
    put(map, ordered_songs, "final_result.mp3", "Final Result");
    put(map, ordered_songs, "until_we_meet_again.mp3", "Until We Meet Again");
    put(map, ordered_songs, "digital_drive.mp3", "Digital Drive");
    put(map, ordered_songs, "safe_haven.mp3", "Safe Haven");

	window.unitedPropertiesIf = {
		getSessionVariable: function(name) {
			var res = localStorage.getItem(name);
			if (res == null) return "";
			return res;
		},
		getProperty: function(name) {
			if (name == "ordered_songs") return "[]";// JSON.stringify(['Hopes and Dreams', 'A Neon Glow Lights the Way', 'Welcome To VA-11 HALL-A', 'Every Day is Night', 'Commencing Simulation', 'Drive Me Wild', 'Good for Health, Bad for Education', 'Who Was I?', 'Troubling News', 'A Gaze That Invited Disaster', 'Friendly Conversation', "You've Got Me", 'Umemoto', "JC Elton's", 'Go! Go! Streaming-chan!', 'All Systems, Go!', 'Where Do I Go From Here?', 'Will You Remember Me?', 'Everything Will Be Okay', 'March of the White Knights', 'A. Rene', 'Neo Avatar', 'Those Who Dwell in The Shadows', 'Nighttime Manuvers', 'A Star Pierces the Darkness', 'Your Love is a Drug', 'Through the Storm We Will Find a Way', 'Synthestitch', 'Snowfall', 'The Answer Lies Within', 'Dawn Approaches', 'With Renewed Hope, We Continue Forward', 'Last Call', 'Reminiscence', 'Believe in Me Who Believes in You', 'Final Result', 'Until We Meet Again', 'Digital Drive', 'Safe Haven']);
			//if (name == "all_themes") return JSON.stringify(["normal", "dotted", "steam", "kira", "meme", "vaporwave"]);
			if (name == "all_themes") return JSON.stringify(["normal", "vaporwave", "noir", "burg", "unity", "empire", "classy", "lain", "neon", "kids", "motif"]);
			if (name == "version_notes") return "Version 4.2.4!\nTap for Patch Notes"
			if (name == "awoo_endpoint") return "https://dangeru.us";
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
		    alert("You are in a web browser! Audio will not continue to the next song at the end of a song, and the music will stop if you close this tab");
			localStorage.setItem("is_playing", "true");
			localStorage.setItem("current_song", name);
			if (typeof(window._audio) != "undefined") {
			    window._audio.pause();
			}
			window._audio = new Audio(map[name]);
			window._audio.play();
		},
		playSound: function(name) {
			new Audio("../../assets/" + name).play();
		},
		launchHTML: function(url) {
			window.open(url.replace("file:///android_res/raw/", ""))
		},
		closeWindow: function(bool) {
			if (bool.toUpperCase() == "TRUE" && typeof(window.opener) != "undefined") {
				window.opener.location.reload();
			}
			window.close();
		},
		toast: function(text) {
			alert(text);
		},
		stopSong: function() {
			localStorage.setItem("is_playing", "false")
			if (typeof(window._audio) != "undefined") {
			    window._audio.pause();
			}
		},
		doAction: function() {
            alert("Cannot perform action - not andorid")
		},
		watchThread: function() {
		    // just do nothing
		}
	}
}

