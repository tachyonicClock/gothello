import React from 'react';

function Cell(props) {
  // Wrapper to the provided onclick handler
  function onClick(e) {
    if (props.legal) {
      e.cell = {}
      e.cell.x = props.x
      e.cell.y = props.y
      props.onClick(e)
    }
  }

  // Displays the cells 'stone'
  var stone = <></>
  if (props.stone !== "none" ) {
    stone = <div className={"Stone " + props.stone}></div>
  }

  return (
  <div
    onClick={onClick}
    style={props.style} 
    className={"Cell " + (props.legal? "legal": "illegal")}>
    {stone}
  </div>)
}

export default Cell;