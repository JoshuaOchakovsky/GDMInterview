# Google DeepMind SWE UX Interview Project - Android

[PRD and instructions](https://docs.google.com/document/d/1bU23kWpfho-HX5z-K0CuBDWoQ9WkgyQl5hDiRdlQiz0/edit?resourcekey=0-PicvY6_PJUzQqHA3Gy-QlQ&tab=t.re9z85qq02x)

[Figma](https://www.figma.com/design/9nAnRIT2fm55KzqbzbEdS9/Design-Exercise?node-id=0-1&p=f&t=xWyF6sMmdPf2kmmw-0)

## Demo

[Demo video](https://www.youtube.com/watch?v=2DjGukTz3hg)

FYI, this demo video does not contain any audio.

## Implementation Notes

### High-level
- Adaptive layout - all dimensions hard-coded, what to do when there's not enough space? What to do when there's lots of extra space available? PRD says this should be "compact and embeddable" so there is definitely more work to be done here to define smaller sizes. Larger sizes are probably not much of a concern.
- Loading state not defined
- Tap targets not defined
- Active states, hover states, etc not defined
- Dark/light theme specs missing

### Details
- Progress bar is at ~39.2% in mock but the times shown suggest ~42.4% progress
- Text style for m:ss times does not match either style in the Type section
- Slider track reaches all the way to the left and right edge of the container's padding, meaning the thumb will extend into the padding when it is all the way to the left or right. Intentional?
- Some unexpected dimensions in the track figma - 2.404dp, 3.165dp. I implemented as specified, but the track should probably all be the same thickness. 3dp?
- Artist string cuts off in spec sooner than expected if the text was allowed to go all the way to the right edge. Intentional?
- Song/artist and album art in mock don't match
- 36px size of previous and next icons seems non-standard, should it be 24 or 40?
- I checked and double-checked all the font, font weight, line height, spacing, etc. on all the typography but the song name is still slightly out of place and more spaced out than the mock. I haven't yet figured out why there is a discrepancy.
- 12x12 thumb seems small, tap target will need to be bigger than that for accessibility purposes. Material really wants it to be at least 16dp, 12 while still being centered required some hacks
- Auto-horizontal-scroll for song title and artist instead of ellipses?
- Right now, only the text scales when font size is increased, but are there any parts of the UI that should be treated as text-like (eg. icons)?

### Figma handoff meta-notes
- Color specs show width, height, border radius and opacity of the swatch tile itself - hide these in future for easier communication with eng
- Google Sans Text is not published by Google to the public, I had to get it from someone who extracted it from GMS (Google Play Services).

### Non-specified things I implemented
- Adaptive layouts for larger and smaller available areas. When smaller, make some paddings, icons, and album art size smaller. When larger, just expand to fit since PRD does not prioritize large sizes.
- Previous vs beginning based on 3s cutoff (comparable to other media players)
- Next button disabled on last track
- Favorite button filled in when track favorited
- Lighter color on the ripple effect for the non-filled icon buttons to stand out more on the dark background
- Repeat button grayed out when not repeating
- Assumed only repeat-all or repeat off
- Play icon when paused
- a11y descriptions
- RTL support - progress bar and media controls don't flip per Material guidance. Album art and song/artist do flip.
- Localized duration

### Nice to add
- Animation on favoriting/unfavoriting
- Persistent buffering (currently clears when going to next/previous song and then returning)
- Audio
- Dynamic colors
- i18n for a11y descriptions
